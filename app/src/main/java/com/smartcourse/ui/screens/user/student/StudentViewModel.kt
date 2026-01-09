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

    private val _myTutors = mutableStateOf<List<Tutor>>(emptyList())
    val myTutors: State<List<Tutor>> = _myTutors

    private val _discoverTutors = mutableStateOf<List<Tutor>>(emptyList())
    val discoverTutors: State<List<Tutor>> = _discoverTutors

    private val _latestChats = mutableStateOf<List<ChatItem>>(emptyList())
    val latestChats: State<List<ChatItem>> = _latestChats

    private var student: Student? = null

    fun load(student: Student) {
        this.student = student
        loadTutors()
        loadChats()
    }

    private fun loadTutors() {
        val s = student ?: return

        viewModelScope.launch {
            val myId = s.user.getUID()

            // Student requested course IDs
            val myCourseIds = s.coursesSeekingHelp.map { it.id }.toSet()

            // Load all users except me, filter tutors (role is nullable)
            val tutorUsers: List<User> = userRepository
                .getAllUsersExcept(myId)
                .filter { it.role == UserRole.TUTOR }

            // Build Tutor domain objects properly (no casts)
            val allTutors: List<Tutor> = tutorUsers.map { u ->
                async {
                    val links = userRepository.getUserCourses(u.userId)
                    val courses = links.mapNotNull { link ->
                        userRepository.getCourseById(link.course_id)
                    }
                    Tutor(
                        user = u,
                        teachingCourses = courses,
                        savedStudentIds = emptySet()
                    )
                }
            }.awaitAll()

            // My tutors = intersection with my courses
            _myTutors.value = allTutors.filter { tutor ->
                tutor.teachingCourses.any { it.id in myCourseIds }
            }

            // Discover = all tutors (you can rank later)
            _discoverTutors.value = allTutors
        }
    }

    private fun loadChats() {
        val s = student ?: return

        viewModelScope.launch {
            _latestChats.value = userRepository.loadRecentChats(s.user.getUID())
        }
    }
}
