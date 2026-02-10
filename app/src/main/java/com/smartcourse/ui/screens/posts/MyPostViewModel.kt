package com.smartcourse.ui.screens.posts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.models.usermodel.Course
import com.smartcourse.data.models.usermodel.Post
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.repositories.user.CourseRepository
import com.smartcourse.data.repositories.user.PostRepository
import com.smartcourse.data.repositories.user.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPostViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val profileRepo: ProfileRepository,
    private val courseRepo: CourseRepository,
    private val postRepo: PostRepository
): ViewModel() {

    private val userId: String = savedStateHandle["userId"] ?: error("Post requires userId")

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _myPosts = MutableStateFlow<List<Post>>(emptyList())
    val myPosts = _myPosts.asStateFlow()

    private val _userCourses = MutableStateFlow<List<Course>>(emptyList())
    val userCourses = _userCourses.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load profile details
                _user.value = profileRepo.loadUser(userId)

                // Fetch and filter posts for this specific user
                val allPosts = postRepo.loadPosts()
                _myPosts.value = allPosts.filter { it.userId == userId }

                // Load courses for the "Add Post" dialog
                _userCourses.value = courseRepo.getUserCourses(userId)
                    .mapNotNull { courseRepo.getCourseById(it.course_id) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            postRepo.deletePost(postId)
            loadInitialData() // Refresh list after deletion
        }
    }

    fun createPost(content: String, selectedCourseIds: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                postRepo.createPost(content, selectedCourseIds)
                loadInitialData() // Refresh list after adding
            } finally {
                _isLoading.value = false
            }
        }
    }
}