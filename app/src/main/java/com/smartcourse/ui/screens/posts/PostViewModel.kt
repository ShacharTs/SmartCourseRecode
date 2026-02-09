package com.smartcourse.ui.screens.posts

import androidx.lifecycle.ViewModel
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(
    authRepository : AuthRepository
): ViewModel() {

    private val currentUser : User? = null
    val currentUserFlow = authRepository.currentUser


}