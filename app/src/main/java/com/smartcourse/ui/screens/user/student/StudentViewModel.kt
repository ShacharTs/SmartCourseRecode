package com.smartcourse.ui.screens.user.student

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.firestore.ListenerRegistration
import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.models.usermodel.Student
import com.smartcourse.data.models.usermodel.Tutor
import com.smartcourse.data.models.usermodel.User
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
class StudentHomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    /* ==============================
       UI STATE
       ============================== */

    private val _myTutors = mutableStateOf<List<Tutor>>(emptyList())
    val myTutors: State<List<Tutor>> = _myTutors

    private val _discoverTutors = mutableStateOf<List<Tutor>>(emptyList())
    val discoverTutors: State<List<Tutor>> = _discoverTutors

    private val _latestChats = mutableStateOf<List<ChatItem>>(emptyList())
    val latestChats: State<List<ChatItem>> = _latestChats

    private var student: Student? = null

    private var chatsListener: ListenerRegistration? = null

    /* ==============================
       ENTRY POINT
       ============================== */

    fun load(student: Student) {
        this.student = student
        loadTutors()
        subscribeToChats()
    }

    /* ==============================
       TUTORS
       ============================== */

    private fun loadTutors() {
        val s = student ?: return

        viewModelScope.launch {
            val myId = s.user.getUID()

            val myCourseIds =
                s.coursesSeekingHelp.map { it.id }.toSet()

            val savedTutorIds =
                userRepository.getFavoriteUserIds(myId)

            val tutorUsers =
                userRepository
                    .getAllUsersExcept(myId)
                    .filter { it.role == UserRole.TUTOR }

            val allTutors =
                tutorUsers.map { user ->
                    async {
                        val links = userRepository.getUserCourses(user.userId)
                        val courses = links.mapNotNull {
                            userRepository.getCourseById(it.course_id)
                        }

                        Tutor(
                            user = user,
                            teachingCourses = courses,
                            savedStudentIds = emptySet()
                        )
                    }
                }.awaitAll()

            _myTutors.value =
                allTutors.filter { it.user.getUID() in savedTutorIds }

            _discoverTutors.value =
                allTutors.filter {
                    it.teachingCourses.any { c -> c.id in myCourseIds } &&
                            it.user.getUID() !in savedTutorIds
                }
        }
    }

    /* ==============================
       CHATS (FIXED)
       ============================== */

    private fun subscribeToChats() {
        val s = student ?: return

        chatsListener?.remove()

        chatsListener =
            chatRepository.listenToUserChats(s.user.getUID()) { chats ->
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


    override fun onCleared() {
        chatsListener?.remove()
        chatsListener = null
    }

    /* ==============================
       CHAT NAVIGATION
       ============================== */

    fun openChatWithTutor(
        tutorId: String,
        navController: NavController
    ) {
        val s = student ?: return

        viewModelScope.launch {
            val chatId =
                chatRepository.ensureChatExists(
                    s.user.getUID(),
                    tutorId
                )

            navController.navigate(
                Screen.ChatRoom.createRoute(chatId)
            )
        }
    }

    /* ==============================
       SAVE USER (FAVORITE)
       ============================== */

    fun saveUser(user: User) {
        val s = student ?: return
        val userA = s.user.getUID()
        val userB = user.getUID()

        viewModelScope.launch {
            userRepository.saveUser(userA, userB)

            val tutorToAdd =
                _discoverTutors.value.firstOrNull {
                    it.user.getUID() == userB
                }

            _discoverTutors.value =
                _discoverTutors.value.filter {
                    it.user.getUID() != userB
                }

            tutorToAdd?.let {
                if (_myTutors.value.none { t -> t.user.getUID() == userB }) {
                    _myTutors.value += it
                }
            }
        }
    }
}
