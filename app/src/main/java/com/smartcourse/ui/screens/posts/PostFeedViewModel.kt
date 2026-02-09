package com.smartcourse.ui.screens.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.models.usermodel.Course
import com.smartcourse.data.models.usermodel.Post
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.user.PostRepository
import com.smartcourse.data.repositories.user.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class PostFeedViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository, // Added private val
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        // Collect the user flow reactively
        viewModelScope.launch {
            authRepository.currentUser.collect { currentUser ->
                if (currentUser != null) {
                    // This will trigger as soon as the user is not null
                    loadPosts(currentUser.role, currentUser.courses)
                }
            }
        }
    }

    fun loadPosts(currentUserRole: UserRole?, myCourseList: List<Course>?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Fetch from Repository
                val rawPosts = postRepository.loadPosts()
                println("DEBUG: Raw posts from DB: ${rawPosts.size}") //

                val userCache = mutableMapOf<String, User>()
                val myCourseIds = myCourseList?.map { it.id } ?: emptyList()

                // 2. Map author details
                val processedPosts = rawPosts.map { post ->
                    val authorProfile = userCache[post.userId] ?: run {
                        val fetchedUser = profileRepository.loadUser(post.userId) // Your existing call
                        if (fetchedUser != null) userCache[post.userId] = fetchedUser
                        fetchedUser
                    }

                    post.copy(
                        userRole = authorProfile?.role ?: UserRole.STUDENT,
                        displayName = authorProfile?.displayName ?: "Unknown",
                        // GRAB THE IMAGE HERE
                        imageUrl = authorProfile?.image
                    )
                }

                // 3. Apply Filter
                val filteredPosts = processedPosts.filter { post ->
                    // If your list is empty, show all courses.
                    // If you have joined courses, only show those.
                    val myIds = myCourseList?.map { it.id } ?: emptyList()
                    val matchesCourse = if (myIds.isEmpty()) true else post.courses.any { it.id in myIds }

                    val isCorrectRole = when (currentUserRole) {
                        UserRole.TUTOR -> post.userRole == UserRole.STUDENT
                        UserRole.STUDENT -> post.userRole == UserRole.TUTOR
                        else -> false
                    }

                    matchesCourse && isCorrectRole
                }

                println("DEBUG: Filtered posts after role/course check: ${filteredPosts.size}") //
                _posts.value = filteredPosts

            } catch (e: Exception) {
                println("DEBUG: Error occurred: ${e.message}") //
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}


