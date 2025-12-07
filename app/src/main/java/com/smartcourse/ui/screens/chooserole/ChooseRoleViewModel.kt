package com.smartcourse.ui.screens.chooserole

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.UserRepository
import kotlinx.coroutines.launch


class ChooseRoleViewModel : ViewModel() {

    private lateinit var userRepository: UserRepository
    lateinit var userObj: User

    fun init(repo: UserRepository, user: User) {
        this.userRepository = repo
        this.userObj = user
    }

    fun updateUserRole(role: UserRole) {
        viewModelScope.launch {
            userRepository.updateUserRole(userObj.getUID(), role)
        }
    }

    fun checkUserRole(user: User): Boolean {
        return user.role != UserRole.TEMP
    }
}
