package com.smartcourse.auth

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository
) : ViewModel() {

    var authState by mutableStateOf(AuthState.LOGGED_OUT)
        private set

    var user by mutableStateOf<User?>(null)
        private set

    var signUpState = mutableStateOf<AuthResult?>(null)

    // ------------------------------------------------------------
    // INITIAL SESSION CHECK
    // ------------------------------------------------------------
    init {
        viewModelScope.launch {
            val existingUser = authRepo.checkExistingSession()

            user = existingUser
            authState = when (existingUser?.role) {
                null, UserRole.TEMP -> AuthState.REGISTERED
                else -> AuthState.LOGGED_IN
            }
        }
    }

    // ------------------------------------------------------------
    // LOGIN (WITH RESULT)
    // ------------------------------------------------------------
    suspend fun loginWithResult(strategy: AuthStrategy, context: Context): Boolean {
        authState = AuthState.LOADING

        val result = strategy.login(context)

        if (!result.success || result.userId == null) {
            authState = AuthState.LOGGED_OUT
            return false
        }

        val profile = authRepo.loadOrCreateUser(result.userId)
        user = profile

        authState = if (profile.role == UserRole.TEMP) {
            AuthState.REGISTERED
        } else {
            AuthState.LOGGED_IN
        }

        return true
    }

    // ------------------------------------------------------------
    // DIRECT LOGIN (WITHOUT RESULT)
    // ------------------------------------------------------------
    fun login(strategy: AuthStrategy, context: Context) {
        viewModelScope.launch {
            loginWithResult(strategy, context)
        }
    }

    // ------------------------------------------------------------
    // REGISTRATION + AUTO LOGIN
    // ------------------------------------------------------------
    fun registerAndLogin(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepo.register(email, password)
            signUpState.value = result

            if (!result.success || result.userId == null) {
                return@launch
            }

            val loginResult = authRepo.login(email, password)
            if (!loginResult.success || loginResult.userId == null) return@launch

            val profile = authRepo.loadOrCreateUser(loginResult.userId)
            user = profile
            authState = AuthState.REGISTERED
        }
    }

    // ------------------------------------------------------------
    // VALIDATION FOR REGISTER SCREEN
    // ------------------------------------------------------------
    fun validateRegistration(email: String, password: String, confirmPassword: String): AuthResult {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            return AuthResult(false, "All fields required")
        }

        if (password.length < 6) {
            return AuthResult(false, "Password must be 6+ chars")
        }

        if (password != confirmPassword) {
            return AuthResult(false, "Passwords do not match")
        }

        return AuthResult(true)
    }

    // ------------------------------------------------------------
    // USER ROLE UPDATE (FROM ChooseRoleScreen)
    // ------------------------------------------------------------
    fun updateUserRole(role: UserRole, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            val u = user ?: return@launch

            // 1. update in DB
            userRepo.updateUserRole(u.getUID(), role)

            // 2. update local ViewModel
            user = u.copy(role = role)

            // 3. mark the user as fully logged in
            authState = AuthState.LOGGED_IN

            onDone?.invoke()
        }
    }


    // ------------------------------------------------------------
    // LOGOUT
    // ------------------------------------------------------------
    fun logout() {
        viewModelScope.launch {
            authRepo.logout()
            user = null
            authState = AuthState.LOGGED_OUT
        }
    }
}
