package com.smartcourse.ui.screens.user.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.models.usermodel.Course
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user


    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses

    fun loadCourses(userId: String) {
        viewModelScope.launch {
            val links = userRepository.getUserCourses(userId)
            val courses = links.mapNotNull {
                userRepository.getCourseById(it.course_id)
            }
            _courses.value = courses
        }
    }

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _user.value = userRepository.loadUser(userId)
        }
    }

    fun updateBio(userId: String, bio: String) {
        viewModelScope.launch {
            userRepository.updateUserBio(userId, bio)
            loadUser(userId)
        }
    }

    fun updateRole(userId: String, role: UserRole) {
        viewModelScope.launch {
            userRepository.updateUserRole(userId, role)
            loadUser(userId)
        }
    }

    fun updateImage(userId: String, image: String) {
        viewModelScope.launch {
            userRepository.updateUserImage(userId, image)
            loadUser(userId)
        }
    }
}
