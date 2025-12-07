package com.smartcourse.auth

import androidx.lifecycle.ViewModel
import com.smartcourse.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    fun debug(): String = userRepository.testRepo()
}
