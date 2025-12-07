package com.smartcourse.auth

data class AuthResult(
    val success: Boolean,
    val userId: String? = null,
    val error: String? = null
)

