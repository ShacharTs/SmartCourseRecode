package com.smartcourse.ui.screens.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.models.usermodel.Post
import com.smartcourse.data.models.usermodel.UserRole
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
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // ADD THIS: Automatically triggers the load when the screen opens
    init {
        loadPosts()
    }


    fun loadPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val rawPosts = postRepository.loadPosts()
                val userCache = mutableMapOf<String, com.smartcourse.data.models.usermodel.User>()

                val postsWithUserInfo = rawPosts.map { post ->
                    val user = userCache[post.userId] ?: run {
                        val fetchedUser = profileRepository.loadUser(post.userId)
                        if (fetchedUser != null) userCache[post.userId] = fetchedUser
                        fetchedUser
                    }

                    post.copy(
                        userRole = user?.role ?: UserRole.TEMP,
                        displayName = user?.displayName ?: "Unknown User" // Map the name here
                    )
                }
                _posts.value = postsWithUserInfo
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}


