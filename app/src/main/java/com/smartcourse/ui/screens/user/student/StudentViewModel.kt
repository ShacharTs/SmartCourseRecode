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
class StudentHomeViewModel @Inject constructor(
    private val socialRepo: SocialRepository,
    private val courseRepo: CourseRepository,
    private val profileRepo: ProfileRepository,
    private val chatRepositoryImpl: ChatRepositoryImpl,
    private val authRepository: AuthRepository
) : ViewModel() {

    // ---- SOURCE OF TRUTH ----
    private var currentUser: User? = null

    val currentUserFlow = authRepository.currentUser

    private val _myTutors = mutableStateOf<List<User>>(emptyList())
    val myTutors: State<List<User>> = _myTutors

    private val _discoverTutors = mutableStateOf<List<User>>(emptyList())
    val discoverTutors: State<List<User>> = _discoverTutors

    private val _latestChats = mutableStateOf<List<ChatItem>>(emptyList())
    val latestChats: State<List<ChatItem>> = _latestChats

    private var chatsListener: ListenerRegistration? = null

    // ---- INIT ----
    init {
        viewModelScope.launch {
            currentUserFlow.collect { user ->
                user ?: return@collect
                val enriched = authRepository.populateUserDetails(user)
                load(enriched)
            }
        }
    }

    // ---- SINGLE ENTRY POINT ----
    fun load(user: User) {
        currentUser = user
        loadTutors(user)
        subscribeToChats(user)
    }

    // ---- TUTORS ----
    private fun loadTutors(user: User) {
        viewModelScope.launch {
            val myId = user.userId
            val myCourseIds = user.courses.map { it.id }.toSet()
            val savedTutorIds = socialRepo.getFavoriteUserIds(myId)

            val tutorUsers = socialRepo
                .getAllUsersExcept(myId)
                .filter { it.role == UserRole.TUTOR }

            val enrichedTutors = tutorUsers.map { tutor ->
                async {
                    val links = courseRepo.getUserCourses(tutor.userId)
                    val courses = links.mapNotNull {
                        courseRepo.getCourseById(it.course_id)
                    }
                    tutor.copy(courses = courses)
                }
            }.awaitAll()

            _myTutors.value = enrichedTutors.filter { it.userId in savedTutorIds }
            _discoverTutors.value = enrichedTutors.filter {
                it.userId !in savedTutorIds &&
                        it.courses.any { c -> c.id in myCourseIds }
            }
        }
    }

    // ---- CHATS ----
    private fun subscribeToChats(user: User) {
        chatsListener?.remove()
        chatsListener = chatRepositoryImpl.listenToUserChats(user.userId) { chats ->
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

    // ---- ACTIONS ----
    fun openChatWithTutor(tutorId: String, navController: NavController) {
        val user = currentUser ?: return
        viewModelScope.launch {
            val chatId = chatRepositoryImpl.ensureChatExists(user.userId, tutorId)
            navController.navigate(Screen.ChatRoom.createRoute(chatId))
        }
    }

    fun saveUser(targetUser: User) {
        val user = currentUser ?: return
        viewModelScope.launch {
            socialRepo.saveUser(user.userId, targetUser.userId)
            loadTutors(user)
        }
    }


    override fun onCleared() {
        chatsListener?.remove()
        super.onCleared()
    }

    fun reload() {
        val user = currentUser ?: return
        loadTutors(user)
    }

}
