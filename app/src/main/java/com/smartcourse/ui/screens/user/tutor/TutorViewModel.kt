package com.smartcourse.ui.screens.user.tutor

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.firestore.ListenerRegistration
import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.chat.ChatRepositoryImpl
import com.smartcourse.data.repositories.user.UserRepository
import com.smartcourse.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TutorHomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepositoryImpl: ChatRepositoryImpl
) : ViewModel() {


    private val _myStudents = mutableStateOf<List<User>>(emptyList())
    val myStudents: State<List<User>> = _myStudents

    private val _latestChats = mutableStateOf<List<ChatItem>>(emptyList())
    val latestChats: State<List<ChatItem>> = _latestChats

    private var currentUser: User? = null // Changed from Tutor?
    private var chatsListener: ListenerRegistration? = null

    fun load(user: User) {
        this.currentUser = user
        loadStudents()
        subscribeToChats()
    }

    private fun loadStudents() {
        val u = currentUser ?: return
        viewModelScope.launch {
            val myId = u.userId

            val studentIds = userRepository.getFavoriteUserIds(myId)

            val studentUsers = userRepository
                .getAllUsersExcept(myId)
                .filter { it.role == UserRole.STUDENT }

            // Enrich each user with their courses directly
            val enrichedStudents = studentUsers.map { student ->
                async {
                    val links = userRepository.getUserCourses(student.userId)
                    val courses = links.mapNotNull { userRepository.getCourseById(it.course_id) }
                    student.copy(courses = courses)
                }
            }.awaitAll()

            _myStudents.value = enrichedStudents.filter { it.userId in studentIds }
        }
    }

    private fun subscribeToChats() {
        val u = currentUser ?: return
        chatsListener?.remove()

        chatsListener = chatRepositoryImpl.listenToUserChats(u.userId) { chats ->
            viewModelScope.launch {
                val enriched = chats.map { chat ->
                    val otherUser = runCatching {
                        userRepository.loadUser(chat.otherUserId)
                    }.getOrNull()
                    chat.copy(otherUser = otherUser)
                }
                _latestChats.value = enriched
            }
        }
    }

    fun openChatWithStudent(studentId: String, navController: NavController) {
        val u = currentUser ?: return
        viewModelScope.launch {
            val chatId = chatRepositoryImpl.ensureChatExists(u.userId, studentId)
            navController.navigate(Screen.ChatRoom.createRoute(chatId))
        }
    }

    override fun onCleared() {
        chatsListener?.remove()
        super.onCleared()
    }
}