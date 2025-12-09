package com.smartcourse.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseRoleViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private lateinit var currentUser: User

    fun setUser(user: User) {
        currentUser = user
    }

    fun updateUserRole(role: UserRole, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            userRepository.updateUserRole(currentUser.getUID(), role)
            onDone()
        }
    }

    fun userHasRole(user: User): Boolean {
        return user.role != UserRole.TEMP
    }
}
