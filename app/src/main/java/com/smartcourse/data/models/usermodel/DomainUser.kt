package com.smartcourse.data.models.usermodel

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel


sealed class DomainUser {
    abstract val user: User

    val id: String get() = user.userId
    val name: String get() = user.name.orEmpty()
    val email: String get() = user.email.orEmpty()
    val image: String get() = user.image.orEmpty()

}


