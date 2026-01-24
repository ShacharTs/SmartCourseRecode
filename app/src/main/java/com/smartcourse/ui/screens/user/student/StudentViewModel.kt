package com.smartcourse.ui.screens.user.student

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
class StudentHomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepositoryImpl: ChatRepositoryImpl
) : ViewModel() {

    // UI state now uses List<User>
    private val _myTutors = mutableStateOf<List<User>>(emptyList())
    val myTutors: State<List<User>> = _myTutors

    private val _discoverTutors = mutableStateOf<List<User>>(emptyList())
    val discoverTutors: State<List<User>> = _discoverTutors

    private val _latestChats = mutableStateOf<List<ChatItem>>(emptyList())
    val latestChats: State<List<ChatItem>> = _latestChats

    // Use the flat User model
    private var currentUser: User? = null
    private var chatsListener: ListenerRegistration? = null

    fun load(user: User) {
        this.currentUser = user
        loadTutors()
        subscribeToChats()
    }

    private fun loadTutors() {
        val s = currentUser ?: return

        viewModelScope.launch {
            val myId = s.userId // Direct access to userId
            val myCourseIds = s.courses.map { it.id }.toSet() // Courses from User object
            val savedTutorIds = userRepository.getFavoriteUserIds(myId)

            val tutorUsers = userRepository
                .getAllUsersExcept(myId)
                .filter { it.role == UserRole.TUTOR }

            val allTutorsEnriched = tutorUsers.map { tutor ->
                async {
                    val links = userRepository.getUserCourses(tutor.userId)
                    val courses = links.mapNotNull {
                        userRepository.getCourseById(it.course_id)
                    }
                    // Enrich the user object with transient course data
                    tutor.copy(courses = courses)
                }
            }.awaitAll()

            _myTutors.value = allTutorsEnriched.filter { it.userId in savedTutorIds }
            _discoverTutors.value = allTutorsEnriched.filter {
                it.courses.any { c -> c.id in myCourseIds } && it.userId !in savedTutorIds
            }
        }
    }

    private fun subscribeToChats() {
        val s = currentUser ?: return
        chatsListener?.remove()

        chatsListener = chatRepositoryImpl.listenToUserChats(s.userId) { chats ->
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

    fun openChatWithTutor(tutorId: String, navController: NavController) {
        val s = currentUser ?: return
        viewModelScope.launch {
            val chatId = chatRepositoryImpl.ensureChatExists(s.userId, tutorId)
            navController.navigate(Screen.ChatRoom.createRoute(chatId))
        }
    }

    fun saveUser(targetUser: User) {
        val s = currentUser ?: return
        viewModelScope.launch {
            userRepository.saveUser(s.userId, targetUser.userId)

            val tutorToAdd = _discoverTutors.value.firstOrNull { it.userId == targetUser.userId }
            _discoverTutors.value = _discoverTutors.value.filter { it.userId != targetUser.userId }


            tutorToAdd?.let {
                if (_myTutors.value.none { t -> t.userId == targetUser.userId }) {
                    _myTutors.value += it
                }
            }
        }
    }

    override fun onCleared() {
        chatsListener?.remove()
        super.onCleared()
    }
}