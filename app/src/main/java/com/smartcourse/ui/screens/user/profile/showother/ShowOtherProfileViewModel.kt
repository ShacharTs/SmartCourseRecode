package com.smartcourse.ui.screens.user.profile.showother

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.models.usermodel.Course
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.ChatRepository
import com.smartcourse.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowOtherProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ShowOtherProfileVM"
    }

    // ------------------------------------------------------------
    // Args
    // ------------------------------------------------------------

    private val userId: String =
        savedStateHandle["userId"]
            ?: error("ShowOtherProfile requires userId")

    // ------------------------------------------------------------
    // State
    // ------------------------------------------------------------

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

    // ------------------------------------------------------------
    // Init
    // ------------------------------------------------------------

    init {
        Log.d(TAG, "init → userId=$userId")
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true

            // Reset state
            _user.value = null
            _courses.value = emptyList()
            _favoritesCount.value = 0
            _isFavorite.value = false
            _isTogglingFavorite.value = false

            try {
                val user = userRepository.loadUser(userId) ?: return@launch
                _user.value = user

                _favoritesCount.value =
                    if (user.role == UserRole.TUTOR)
                        userRepository.countUserFavorites(userId)
                    else 0

                _courses.value =
                    userRepository.getUserCourses(userId)
                        .mapNotNull { userRepository.getCourseById(it.course_id) }

                val me = authRepository.currentUser.value?.userId
                _isFavorite.value =
                    me?.let { userRepository.isUserFavorite(it, userId) } ?: false

            } finally {
                _isLoading.value = false
            }
        }
    }


    fun toggleFavorite() {
        val target = _user.value ?: return
        val me = authRepository.currentUser.value?.userId ?: return
        if (_isTogglingFavorite.value) return

        viewModelScope.launch {
            _isTogglingFavorite.value = true

            val wasFavorite = _isFavorite.value
            val nextValue = !wasFavorite

            // optimistic update
            applyFavoriteState(nextValue)

            try {
                if (nextValue) {
                    userRepository.saveUser(me, target.id)
                } else {
                    userRepository.unsaveUser(me, target.id)
                }
            } catch (e: Exception) {
                Log.e(TAG, "toggleFavorite FAILED → rollback", e)
                applyFavoriteState(wasFavorite)
            } finally {
                _isTogglingFavorite.value = false
            }
        }
    }

    private fun applyFavoriteState(favorite: Boolean) {
        _isFavorite.value = favorite
        _favoritesCount.value =
            if (favorite) _favoritesCount.value + 1
            else (_favoritesCount.value - 1).coerceAtLeast(0)

        Log.d(
            TAG,
            "applyFavoriteState → isFavorite=$favorite favoritesCount=${_favoritesCount.value}"
        )
    }

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
