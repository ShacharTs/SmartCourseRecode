package com.smartcourse.ui.screens.user.tutor

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.firestore.ListenerRegistration
import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.models.usermodel.Student
import com.smartcourse.data.models.usermodel.Tutor
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.ChatRepository
import com.smartcourse.data.repositories.UserRepository
import com.smartcourse.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TutorHomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _myStudents = mutableStateOf<List<Student>>(emptyList())
    val myStudents: State<List<Student>> = _myStudents

    private val _latestChats = mutableStateOf<List<ChatItem>>(emptyList())
    val latestChats: State<List<ChatItem>> = _latestChats

    private var tutor: Tutor? = null
    private var chatsListener: ListenerRegistration? = null

    fun load(tutor: Tutor) {
        this.tutor = tutor
        loadStudents()
        subscribeToChats()
    }

    private fun loadStudents() {
        val t = tutor ?: return
        viewModelScope.launch {
            val myId = t.user.getUID()

            // Fetch IDs of students who have "saved" or interacted with this tutor
            val studentIds = userRepository.getFavoriteUserIds(myId)

            val studentUsers = userRepository
                .getAllUsersExcept(myId)
                .filter { it.role == UserRole.STUDENT }

            val allStudents = studentUsers.map { user ->
                async {
                    val links = userRepository.getUserCourses(user.userId)
                    val courses = links.mapNotNull { userRepository.getCourseById(it.course_id) }

                    Student(
                        user = user,
                        coursesSeekingHelp = courses
                    )
                }
            }.awaitAll()

            // Filter for students relevant to this tutor
            _myStudents.value = allStudents.filter { it.user.getUID() in studentIds }
        }
    }

    private fun subscribeToChats() {
        val t = tutor ?: return
        chatsListener?.remove()

        chatsListener = chatRepository.listenToUserChats(t.user.getUID()) { chats ->
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
        val t = tutor ?: return
        viewModelScope.launch {
            val chatId = chatRepository.ensureChatExists(t.user.getUID(), studentId)
            navController.navigate(Screen.ChatRoom.createRoute(chatId))
        }
    }

    override fun onCleared() {
        chatsListener?.remove()
        super.onCleared()
    }
}