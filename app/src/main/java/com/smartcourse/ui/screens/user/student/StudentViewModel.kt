//package com.smartcourse.viewmodels
//
//import androidx.compose.runtime.State
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.navigation.NavController
//import com.smartcourse.data.models.chat.ChatItem
//import com.smartcourse.data.models.usermodel.User
//import com.smartcourse.data.models.usermodel.UserRole
//import com.smartcourse.data.repositories.ChatRepository
//import com.smartcourse.data.repositories.UserRepository
//import com.smartcourse.navigation.Screen
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class StudentViewModel @Inject constructor(
//    private val userRepository: UserRepository,
//    private val chatRepository: ChatRepository
//) : ViewModel() {
//
//    private lateinit var currentUser: User
//
//    fun setUser(user: User) {
//        currentUser = user
//    }
//
//
//
//
//
//    private val _matches = mutableStateOf<List<User>>(emptyList())
//    val matches: State<List<User>> = _matches
//
//    val recommendedTutors: State<List<User>> = matches
//
//
//    private val _recentChats = mutableStateOf<List<ChatItem>>(emptyList())
//    val recentChats: State<List<ChatItem>> = _recentChats
//
//    fun loadMatchingTutors() {
//        viewModelScope.launch {
//            val myId = currentUser.getUID()
//
//            val candidates = userRepository.matchUserByRole(myId, UserRole.TUTOR)
//            val myCourseIds = loadCoursesForUser(myId).toSet()
//            val filtered = enrichUsersWithSharedCourses(candidates, myCourseIds)
//
//            _matches.value = filtered
//        }
//    }
//
//    fun loadRecentChats() {
//        viewModelScope.launch {
//            _recentChats.value = userRepository.loadRecentChats(currentUser.getUID())
//        }
//    }
//
//    private suspend fun loadCoursesForUser(userId: String): List<String> {
//        return userRepository.getUserCourses(userId).map { it.course_id }
//    }
//
//    private suspend fun enrichUsersWithSharedCourses(
//        users: List<User>,
//        myCourseIds: Set<String>
//    ): List<User> {
//
//        val result = mutableListOf<User>()
//
//        for (user in users) {
//            val theirCourseIds = loadCoursesForUser(user.getUID()).toSet()
//
//            val sharedIds = myCourseIds.intersect(theirCourseIds)
//            if (sharedIds.isNotEmpty()) {
//                val sharedCourses = sharedIds.mapNotNull { id ->
//                    userRepository.getCourseById(id)
//                }
//
//                user.courses = sharedCourses
//                result.add(user)
//            }
//        }
//
//        return result
//    }
//
//    fun openChatWith(otherUserId: String, navController: NavController) {
//        viewModelScope.launch {
//            val chatId = chatRepository.ensureChatExists(currentUser.getUID(), otherUserId)
//            navController.navigate(Screen.ChatRoom.createRoute(chatId))
//        }
//    }
//
//    fun loadMatchingTutors(myId: String) {
//        viewModelScope.launch {
//
//            // STEP 1: load ALL tutors
//            val candidates = userRepository.matchUserByRole(myId, UserRole.TUTOR)
//
//            // STEP 2: load MY course IDs ONCE
//            val myCourseIds = loadCoursesForUser(myId).toSet()
//
//            // STEP 3: enrich + filter by shared courses
//            val filteredMatches = enrichUsersWithSharedCourses(candidates, myCourseIds)
//
//            _matches.value = filteredMatches
//        }
//    }
//}

package com.smartcourse.ui.screens.user.student

import androidx.lifecycle.ViewModel
import com.smartcourse.data.models.usermodel.Student
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


//@HiltViewModel
//class StudentHomeViewModel @Inject constructor(
//    authRepository: AuthRepository,
//    private val userRepository: UserRepository
//) : ViewModel() {
//
//    val user = authRepository.currentUser.value
//
//
//    init {
//        require(user?.role == UserRole.STUDENT)
//        loadTutors()
//    }
//
//    private fun loadTutors() {
//        // discovery logic
//    }
//}

@HiltViewModel
class StudentHomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private lateinit var student: Student

    fun setStudent(student: Student) {
        this.student = student
        loadTutors()
    }

    private fun loadTutors() {
        // use student.user.userId
        // use student.coursesSeekingHelp
    }
}


