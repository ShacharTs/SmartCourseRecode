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
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.chat.ChatRepositoryImpl
import com.smartcourse.data.repositories.user.CourseRepository
import com.smartcourse.data.repositories.user.ProfileRepository
import com.smartcourse.data.repositories.user.SocialRepository
import com.smartcourse.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TutorHomeViewModel @Inject constructor(
    private val socialRepo: SocialRepository,
    private val courseRepo: CourseRepository,
    private val profileRepo: ProfileRepository,
    private val chatRepositoryImpl: ChatRepositoryImpl,
    private val authRepository: AuthRepository
) : ViewModel() {


    private var currentUser: User? = null
    private var chatsListener: ListenerRegistration? = null

    private val _myStudents = mutableStateOf<List<User>>(emptyList())
    val myStudents: State<List<User>> = _myStudents

    private val _latestChats = mutableStateOf<List<ChatItem>>(emptyList())
    val latestChats: State<List<ChatItem>> = _latestChats


    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                user ?: return@collect
                load(user)
            }
        }
    }


    fun load(user: User) {
        currentUser = user
        loadStudents()
        subscribeToChats()
    }

    fun reload() {
        val u = currentUser ?: return
        loadStudents()
    }

    private fun loadStudents() {
        val u = currentUser ?: return
        viewModelScope.launch {
            val myId = u.userId

            val studentIds = socialRepo.getFavoriteUserIds(myId)

            val studentUsers = socialRepo
                .getAllUsersExcept(myId)
                .filter { it.role == UserRole.STUDENT }

            // Enrich each user with their courses directly
            val enrichedStudents = studentUsers.map { student ->
                async {
                    val links = courseRepo.getUserCourses(student.userId)
                    val courses = links.mapNotNull { courseRepo.getCourseById(it.course_id) }
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
                        profileRepo.loadUser(chat.otherUserId)
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