package com.smartcourse.ui.screens.user.profile.showother

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
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
        emit(userRepository.loadUser(userId))
    }
}
