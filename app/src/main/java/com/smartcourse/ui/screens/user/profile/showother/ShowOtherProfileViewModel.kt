package com.smartcourse.ui.screens.user.profile.showother

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.models.usermodel.Course
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.chat.ChatRepositoryImpl
import com.smartcourse.data.repositories.user.CourseRepository
import com.smartcourse.data.repositories.user.ProfileRepository
import com.smartcourse.data.repositories.user.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowOtherProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val profileRepo: ProfileRepository,
    private val courseRepo: CourseRepository,
    private val socialRepo: SocialRepository,
    private val chatRepository: ChatRepositoryImpl,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val userId: String =
        savedStateHandle["userId"]
            ?: error("ShowOtherProfile requires userId")

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _favoritesCount = MutableStateFlow(0)
    val favoritesCount: StateFlow<Int> = _favoritesCount

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses

    private val _isTogglingFavorite = MutableStateFlow(false)
    val isTogglingFavorite: StateFlow<Boolean> = _isTogglingFavorite


    init {
        loadInitialData()
    }

    /* ============================================================
       INITIAL LOAD
       ============================================================ */

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                loadUser()
                loadCourses()
                loadFavoriteState()
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

    private suspend fun loadFavoriteState() {
        val me = requireMe()
        _isFavorite.value = socialRepo.isUserFavorite(me, userId)
        _favoritesCount.value =
            socialRepo.countUserFavorites(userId)
    }

    /* ============================================================
       TOGGLE FAVORITE
       ============================================================ */

    fun toggleFavorite() {
        if (_isTogglingFavorite.value) return
        val target = _user.value ?: return

        viewModelScope.launch {
            _isTogglingFavorite.value = true
            try {
                val me = requireMe()
                val targetUserId = target.userId

                val wasFavorite = _isFavorite.value
                performFavoriteAction(me, targetUserId, wasFavorite)

                // optimistic for icon only
                _isFavorite.value = !wasFavorite

                // re-sync from DB (single source of truth)
                refreshFavoriteCount(targetUserId)

            } finally {
                _isTogglingFavorite.value = false
            }
        }
    }

    private suspend fun performFavoriteAction(
        me: String,
        targetUserId: String,
        wasFavorite: Boolean
    ) {
        if (wasFavorite) {
            socialRepo.unsaveUser(me, targetUserId)
        } else {
            socialRepo.saveUser(me, targetUserId)
        }
    }

    private suspend fun refreshFavoriteCount(targetUserId: String) {
        _favoritesCount.value =
            socialRepo.countUserFavorites(targetUserId)
    }

    /* ============================================================
       HELPERS
       ============================================================ */

    private suspend fun requireMe(): String =
        authRepository.currentUser
            .filterNotNull()
            .first()
            .userId

    fun openChat(onReady: (chatId: String) -> Unit) {
        viewModelScope.launch {
            val me = authRepository.currentUser.value?.userId ?: return@launch
            val chatId = chatRepository.ensureChatExists(
                userA = me,
                userB = userId
            )
            onReady(chatId)
        }
    }
}


