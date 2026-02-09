package com.smartcourse.ui.screens.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    authRepository: AuthRepository,
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val user  = authRepository.currentUser
    val role = user.value?.role


    init{
        loadPosts(role)
    }


    fun loadPosts(currentUserRole: UserRole?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val rawPosts = postRepository.loadPosts()
                val userCache = mutableMapOf<String,User>()

                val processedPosts = rawPosts.map { post ->
                    val user = userCache[post.userId] ?: run {
                        val fetchedUser = profileRepository.loadUser(post.userId)
                        if (fetchedUser != null) userCache[post.userId] = fetchedUser
                        fetchedUser
                    }
                    post.copy(
                        userRole = user?.role ?: UserRole.TEMP,
                        displayName = user?.displayName ?: "Unknown"
                    )
                }

                // Filter based on the role provided by the UI
                val filteredPosts = when (currentUserRole) {
                    UserRole.TUTOR -> processedPosts.filter { it.userRole == UserRole.STUDENT }
                    UserRole.STUDENT -> processedPosts.filter { it.userRole == UserRole.TUTOR }
                    else -> emptyList()
                }

                _posts.value = filteredPosts
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}


