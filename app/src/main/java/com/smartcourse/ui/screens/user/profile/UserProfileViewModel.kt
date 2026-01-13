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

    // --------------------------------------------------
    // USER STATE
    // --------------------------------------------------

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    // --------------------------------------------------
    // COURSES STATE
    // --------------------------------------------------

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses


    private val _allCourses = MutableStateFlow<List<Course>>(emptyList())
    val allCourses: StateFlow<List<Course>> = _allCourses

    // --------------------------------------------------
    // LOADERS
    // --------------------------------------------------

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _user.value = userRepository.loadUser(userId)
        }
    }

    fun loadCourses(userId: String) {
        viewModelScope.launch {
            val links = userRepository.getUserCourses(userId)
            _courses.value = links.mapNotNull {
                userRepository.getCourseById(it.course_id)
            }
        }
    }

    // --------------------------------------------------
    // UPDATES — USER
    // --------------------------------------------------

    fun updateName(userId: String, name: String) {
        if (name.isBlank()) return

        viewModelScope.launch {
            userRepository.updateUserName(userId, name)
            loadUser(userId)
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
        if (image.isBlank()) return

        viewModelScope.launch {
            userRepository.updateUserImage(userId, image)
            loadUser(userId)
        }
    }

    // --------------------------------------------------
    // UPDATES — COURSES
    // --------------------------------------------------

    fun removeCourse(userId: String, courseId: String) {
        viewModelScope.launch {
            userRepository.removeUserCourse(userId, courseId)
            loadCourses(userId)
        }
    }



    fun loadAllCourses() {
        viewModelScope.launch {
            _allCourses.value = userRepository.getAllCourses()
        }
    }

    fun addCourse(userId: String, courseId: String) {
        viewModelScope.launch {
            userRepository.addUserCourse(userId, courseId)
            loadCourses(userId)
        }
    }
}

