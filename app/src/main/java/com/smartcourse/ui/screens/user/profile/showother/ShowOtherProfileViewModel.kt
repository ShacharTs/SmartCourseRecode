package com.smartcourse.ui.screens.user.profile.showother

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


@HiltViewModel
class ShowOtherProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository
) : ViewModel() {

    private val userId: String =
        savedStateHandle["userId"]
            ?: error("ShowOtherProfile requires userId")

    val user = flow {
        emit(
            userRepository.loadUser(userId)
                ?: error("User not found: $userId")
        )
    }

    val favoritesCount = flow {
        val u = userRepository.loadUser(userId)
            ?: error("User not found: $userId")

        if (u.role == UserRole.TUTOR) {
            emit(userRepository.countUserFavorites(u.id))
        } else {
            emit(null)
        }
    }
}




