package com.smartcourse.auth

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID
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
    // INIT — FIXED FOR SUPABASE V3
    // ------------------------------------------------------------
    init {
        viewModelScope.launch {
            authState = AuthState.LOADING

            val session = authRepo.client.auth.currentSessionOrNull()
            if (session == null) {
                authState = AuthState.LOGGED_OUT
                return@launch
            }

            // Supabase v3: must always try refresh, refreshToken is not stored in session object
            val refreshed = runCatching {
                authRepo.client.auth.refreshCurrentSession()
            }.isSuccess

            if (!refreshed) {
                authState = AuthState.LOGGED_OUT
                return@launch
            }

            val sessionUser = authRepo.client.auth.currentUserOrNull()
            if (sessionUser == null) {
                authState = AuthState.LOGGED_OUT
                return@launch
            }

            val profile = authRepo.loadOrCreateUser(sessionUser.id)
            user = profile

            authState =
                if (profile.role == UserRole.TEMP) AuthState.REGISTERED
                else AuthState.LOGGED_IN

            Log.d("AuthViewModel", "INIT → Restored user ${profile.email}, role=${profile.role}")
        }
    }

    // ------------------------------------------------------------
    // EMAIL LOGIN
    // ------------------------------------------------------------
    suspend fun loginEmail(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                authState = AuthState.LOADING

                val uid = authRepo.loginEmail(email, password)
                if (uid == null) {
                    authState = AuthState.LOGGED_OUT
                    return@withContext false
                }

                val profile = authRepo.loadOrCreateUser(uid)
                user = profile

                authState =
                    if (profile.role == UserRole.TEMP) AuthState.REGISTERED
                    else AuthState.LOGGED_IN

                Log.d("AuthViewModel", "loginEmail: ${profile.email}, state=${authState.name}")

                return@withContext true
            } catch (e: Exception) {
                Log.e("AuthViewModel", "loginEmail ERROR", e)
                authState = AuthState.LOGGED_OUT
                return@withContext false
            }
        }
    }

    // ------------------------------------------------------------
    // REGISTER
    // ------------------------------------------------------------
    suspend fun registerEmail(email: String, password: String): Boolean {
        return try {
            val uid = authRepo.registerEmail(email, password)

            signUpState.value = AuthResult(uid != null, userId = uid)
            uid != null
        } catch (e: Exception) {
            signUpState.value = AuthResult(false, error = e.localizedMessage)
            false
        }
    }

    // ------------------------------------------------------------
    // GOOGLE — REQUEST ID TOKEN
    // ------------------------------------------------------------
    private suspend fun requestGoogleIdToken(context: Context): Pair<String, String>? {
        return try {
            val cm = CredentialManager.create(context)

            val rawNonce = UUID.randomUUID().toString()
            val hashed = MessageDigest.getInstance("SHA-256")
                .digest(rawNonce.toByteArray())
                .joinToString("") { "%02x".format(it) }

            val googleOpt = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setAutoSelectEnabled(false)
                .setServerClientId("332919763204-p0vp92hoab97p7brdtrr60cnjr9hidod.apps.googleusercontent.com")
                .setNonce(hashed)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleOpt)
                .build()

            val result = cm.getCredential(context, request)
            val cred = GoogleIdTokenCredential.createFrom(result.credential.data)

            Pair(cred.idToken, rawNonce)
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Google ID request failed", e)
            null
        }
    }

    // ------------------------------------------------------------
    // GOOGLE LOGIN
    // ------------------------------------------------------------
    fun loginGoogle(context: Context) {
        viewModelScope.launch {
            authState = AuthState.LOADING

            val (idToken, nonce) = requestGoogleIdToken(context) ?: run {
                authState = AuthState.LOGGED_OUT
                return@launch
            }

            val uid = authRepo.loginGoogle(idToken, nonce)
            if (uid == null) {
                authState = AuthState.LOGGED_OUT
                return@launch
            }

            val profile = authRepo.loadOrCreateUser(uid)
            user = profile

            authState =
                if (profile.role == UserRole.TEMP) AuthState.REGISTERED
                else AuthState.LOGGED_IN
        }
    }

    // ------------------------------------------------------------
    // UPDATE ROLE
    // ------------------------------------------------------------
    fun updateUserRole(role: UserRole, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            val u = user ?: return@launch
            userRepo.updateUserRole(u.getUID(), role)
            user = u.copy(role = role)
            authState = AuthState.LOGGED_IN
            onDone?.invoke()
        }
    }

    // ------------------------------------------------------------
    // REGISTRATION VALIDATION
    // ------------------------------------------------------------
    fun validateRegistration(
        email: String,
        password: String,
        confirmPassword: String
    ): AuthResult {

        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank())
            return AuthResult(false, "All fields are required")

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return AuthResult(false, "Invalid email address")

        if (password.length < 6)
            return AuthResult(false, "Password must be at least 6 characters")

        if (password != confirmPassword)
            return AuthResult(false, "Passwords do not match")

        return AuthResult(true)
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
