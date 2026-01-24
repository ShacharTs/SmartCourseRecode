package com.smartcourse.ui.screens.login

import android.content.Context
import androidx.lifecycle.ViewModel
import com.smartcourse.auth.AuthStrategy
import com.smartcourse.auth.EmailAuthStrategy
import com.smartcourse.auth.GoogleAuthStrategy
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository,
    private val supabase: SupabaseClient
) : ViewModel() {

    suspend fun loginWithEmail(
        email: String,
        password: String,
        context: Context
    ): Result<Unit> {

        val cleanEmail = email.trim()
        val cleanPassword = password.trim()

        if (cleanEmail.isEmpty()) {
            return Result.failure(Exception("Email is required"))
        }

        if (cleanPassword.isEmpty()) {
            return Result.failure(Exception("Password is required"))
        }

        val strategy = EmailAuthStrategy(
            email = cleanEmail,
            password = cleanPassword,
            supabase = supabase
        )

        return performLogin(strategy, context)
    }

    suspend fun loginWithGoogle(context: Context): Result<Unit> {
        val strategy = GoogleAuthStrategy(
            supabase = supabase
        )

        return performLogin(strategy, context)
    }

    private suspend fun performLogin(
        strategy: AuthStrategy,
        context: Context
    ): Result<Unit> {

        val result = authRepo.loginWith(strategy, context)

        if (!result.success || result.userId == null) {
            return Result.failure(
                Exception(result.error ?: "Login failed")
            )
        }

        // THIS is enough
        userRepo.syncGoogleAvatar()
        authRepo.loadOrCreateUser(result.userId)

        return Result.success(Unit)
    }

}
