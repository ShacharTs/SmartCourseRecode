package com.smartcourse.ui.screens.user.tutor

import androidx.lifecycle.ViewModel
import com.smartcourse.data.models.usermodel.Tutor
import com.smartcourse.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//@HiltViewModel
//class TutorViewModel @Inject constructor(
//    private val userRepository: UserRepository,
//    private val chatRepository: ChatRepository
//) : ViewModel() {
//
//    private lateinit var currentUser: User
//
//    fun setUser(user: User) {
//        this.currentUser = user
//    }
//
//    private val _students = mutableStateOf<List<User>>(emptyList())
//    val students: State<List<User>> = _students
//
//    private val _recentChats = mutableStateOf<List<ChatItem>>(emptyList())
//    val recentChats: State<List<ChatItem>> = _recentChats
//
//    fun loadMyStudents() {
//        viewModelScope.launch {
//            // _students.value = userRepository.getStudentsForTutor(currentUser.getUID())
//        }
//    }
//
//    fun loadRecentChats() {
//        viewModelScope.launch {
//            _recentChats.value = userRepository.loadRecentChats(currentUser.getUID())
//        }
//    }
//
//    fun openChatWith(otherUserId: String, myNav: NavController) {
//        viewModelScope.launch {
//            //val chatId = chatRepository.ensureChat(currentUser.getUID(), otherUserId)
//            myNav.navigate(Screen.ChatRoom.route)
//        }
//    }
//}


@HiltViewModel
class TutorHomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private var tutor: Tutor? = null

    fun load(tutor: Tutor) {
        this.tutor = tutor
        loadStudents()
    }

    private fun loadStudents() {
        val t = tutor ?: return
        val courses = t.teachingCourses
        // SQL / repo logic later
    }
}
