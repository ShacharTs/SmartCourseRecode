package com.smartcourse.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.repositories.ChatRepository
import com.smartcourse.data.repositories.UserRepository
import com.smartcourse.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
