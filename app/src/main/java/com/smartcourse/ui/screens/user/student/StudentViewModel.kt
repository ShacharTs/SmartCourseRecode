package com.smartcourse.ui.screens.user.student

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.models.usermodel.Student
import com.smartcourse.data.models.usermodel.Tutor
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentHomeViewModel @Inject constructor(
    private val userRepository: UserRepository
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

    /* ==============================
       ENTRY POINT
       ============================== */

    fun load(student: Student) {
        this.student = student
        loadTutors()
        loadChats()
    }

    /* ==============================
       TUTORS LOGIC
       ============================== */

    private fun loadTutors() {
        val s = student ?: return

        viewModelScope.launch {
            val myId = s.user.getUID()

            // Courses the student needs help with
            val myCourseIds: Set<String> =
                s.coursesSeekingHelp.map { it.id }.toSet()

            // Tutors the student already saved (favorites)
            val savedTutorIds: Set<String> = userRepository.getFavoriteTutorIds(myId)


            // Load all users except me, filter only tutors
            val tutorUsers: List<User> =
                userRepository
                    .getAllUsersExcept(myId)
                    .filter { it.role == UserRole.TUTOR }

            // Build Tutor domain objects in parallel
            val allTutors: List<Tutor> =
                tutorUsers.map { user ->
                    async {
                        val links = userRepository.getUserCourses(user.userId)
                        val courses = links.mapNotNull { link ->
                            userRepository.getCourseById(link.course_id)
                        }

                        Tutor(
                            user = user,
                            teachingCourses = courses,
                            savedStudentIds = emptySet()
                        )
                    }
                }.awaitAll()

            /* ==============================
               MY TUTORS
               ============================== */
            // Only tutors the student already saved
            _myTutors.value =
                allTutors.filter { tutor ->
                    tutor.user.getUID() in savedTutorIds
                }

            /* ==============================
               DISCOVER TUTORS
               ============================== */
            // Tutors that:
            // 1. Teach at least one needed course
            // 2. Are NOT already saved
            _discoverTutors.value =
                allTutors.filter { tutor ->
                    tutor.teachingCourses.any { it.id in myCourseIds } &&
                            tutor.user.getUID() !in savedTutorIds
                }
        }
    }

    /* ==============================
       CHATS
       ============================== */

    private fun loadChats() {
        val s = student ?: return

        viewModelScope.launch {
            _latestChats.value =
                userRepository
                    .loadRecentChats(s.user.getUID())
                    .sortedByDescending { it.lastTimestamp ?: 0L }
                    .take(3)
        }
    }
}
