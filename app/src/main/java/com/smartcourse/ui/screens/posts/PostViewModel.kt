package com.smartcourse.ui.screens.posts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.models.usermodel.Course
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.user.CourseRepository
import com.smartcourse.data.repositories.user.PostRepository
import com.smartcourse.data.repositories.user.ProfileRepository
import com.smartcourse.data.repositories.user.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val profileRepo: ProfileRepository,
    private val courseRepo: CourseRepository,
    private val socialRepo: SocialRepository,
    private val postRepo : PostRepository,
    private val authRepository: AuthRepository
): ViewModel() {

    private val userId: String =
        savedStateHandle["userId"]
            ?: error("Post requires userId")

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses


    init {
        loadInitialData()
    }


    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                loadUser()
                loadCourses()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadUser() {
        val targetUser = profileRepo.loadUser(userId)
            ?: error("User not found")
        _user.value = targetUser
    }

    private suspend fun loadCourses() {
        _courses.value = courseRepo
            .getUserCourses(userId)
            .mapNotNull { courseRepo.getCourseById(it.course_id) }
    }




}


