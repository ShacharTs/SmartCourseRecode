package com.smartcourse.ui.screens.user.profile.showother

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

    // ------------------------------------------------------------
    //  STATE
    // ------------------------------------------------------------

    private val userId: String =
        savedStateHandle["userId"]
            ?: error("ShowOtherProfile requires userId")

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _favoritesCount = MutableStateFlow(0)
    val favoritesCount: StateFlow<Int> = _favoritesCount

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private val favoriteRefresh = MutableStateFlow(0)


    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses

    // ------------------------------------------------------------
    //  INIT
    // ------------------------------------------------------------

    init {
        observeFavoriteState()

        viewModelScope.launch {
            loadUser()
            loadFavoritesIfNeeded()
            loadCourses()
        }
    }



    // ------------------------------------------------------------
    //  LOADERS
    // ------------------------------------------------------------

    private fun loadAll() {
        viewModelScope.launch {
            loadUser()
            loadFavoritesIfNeeded()
            loadCourses()
        }
    }


    private suspend fun loadUser() {
        _user.value =
            userRepository.loadUser(userId)
                ?: error("User not found: $userId")
    }

    private suspend fun loadFavoritesIfNeeded() {
        val u = _user.value ?: return

        if (u.role == UserRole.TUTOR) {
            _favoritesCount.value =
                userRepository.countUserFavorites(u.id)
        }
    }

    private suspend fun loadCourses() {
        val userCourses = userRepository.getUserCourses(userId)

        _courses.value =
            userCourses.mapNotNull { uc ->
                userRepository.getCourseById(uc.course_id)
            }
    }

    fun openChat(
        onReady: (chatId: String) -> Unit
    ) {
        viewModelScope.launch {
            val me = authRepository.currentUser.value?.userId
                ?: return@launch   // או error("Not authenticated")

            val chatId = chatRepository.ensureChatExists(
                userA = me,
                userB = userId
            )

            onReady(chatId)
        }
    }

    fun toggleFavorite() {
        val target = _user.value ?: return

        viewModelScope.launch {
            val me = authRepository.currentUser.value?.userId ?: return@launch

            val newValue = !_isFavorite.value
            _isFavorite.value = newValue

            if (newValue) {
                userRepository.saveUser(me, target.id)
                _favoritesCount.value += 1
            } else {
                userRepository.unsaveUser(me, target.id)
                _favoritesCount.value =
                    (_favoritesCount.value - 1).coerceAtLeast(0)
            }
        }
    }




    private fun observeFavoriteState() {
        viewModelScope.launch {
            val me = authRepository.currentUser.value?.userId ?: return@launch
            val target = userId

            _isFavorite.value =
                userRepository.isUserFavorite(me, target)
        }
    }









}
