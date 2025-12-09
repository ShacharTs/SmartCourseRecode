package com.smartcourse.auth

import android.content.Context

/**
 * Represents the result of an authentication attempt
 * To support Firebase and Supabase
 */
interface AuthStrategy {
    suspend fun login(context: Context): AuthResult

    suspend fun logout()
}
