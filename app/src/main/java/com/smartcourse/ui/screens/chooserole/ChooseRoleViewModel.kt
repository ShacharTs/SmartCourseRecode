package com.smartcourse.ui.screens.chooserole

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseRoleViewModel @Inject constructor(
    authRepo: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val currentUser = authRepo.currentUser

    fun updateUserRole(role: UserRole, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            val user = currentUser.value
                ?: error("No logged-in user")

            userRepository.updateUserRole(
                id = user.userId,
                role = role
            )

            onDone()
        }
    }
}