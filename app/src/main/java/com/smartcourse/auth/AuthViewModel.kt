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

    init {
        viewModelScope.launch {
            val existingSession = authRepo.client.auth.currentSessionOrNull()

            if (existingSession == null) {
                authState = AuthState.LOGGED_OUT
                return@launch
            }

            val hasRefresh = existingSession.refreshToken != null
            if (hasRefresh) {
                runCatching { authRepo.client.auth.refreshCurrentSession() }
            }

            val sessionUser = authRepo.client.auth.currentUserOrNull()
            if (sessionUser == null) {
                authState = AuthState.LOGGED_OUT
                return@launch
            }

            val profile = authRepo.loadOrCreateUser(sessionUser.id)
            user = profile

            authState = if (profile.role == UserRole.TEMP)
                AuthState.REGISTERED
            else
                AuthState.LOGGED_IN
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



                authState = if (profile.role == UserRole.TEMP)
                    AuthState.REGISTERED
                else
                    AuthState.LOGGED_IN


                Log.d("AuthViewModel", "loginEmail: ${profile.name}")
                Log.d("AuthViewModel", "loginEmail: ${profile.role}")
                Log.d("AuthViewModel", "loginEmail: ${profile.email}")
                Log.d("AuthViewModel", "loginEmail: ${authState.name}")

                return@withContext true



            } catch (e: Exception) {
                authState = AuthState.LOGGED_OUT
                return@withContext false
            }
        }
    }


    // ------------------------------------------------------------
    // REGISTER + LOGIN
    // ------------------------------------------------------------
    suspend fun registerEmail(email: String, password: String): Boolean {
        return try {
            val uid = authRepo.registerEmail(email, password)

            signUpState.value = AuthResult(
                success = uid != null,
                userId = uid
            )

            uid != null
        } catch (e: Exception) {
            signUpState.value = AuthResult(
                success = false,
                error = e.localizedMessage
            )
            false
        }
    }


    // ------------------------------------------------------------
    // GOOGLE — REQUEST ID TOKEN (moved from GoogleAuthStrategy)
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

            authState = if (profile.role == UserRole.TEMP)
                AuthState.REGISTERED else AuthState.LOGGED_IN
        }
    }

    fun updateUserRole(role: UserRole, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            val u = user ?: return@launch
            userRepo.updateUserRole(u.getUID(), role)
            user = u.copy(role = role)
            authState = AuthState.LOGGED_IN
            onDone?.invoke()
        }
    }


    fun validateRegistration(
        email: String,
        password: String,
        confirmPassword: String
    ): AuthResult {

        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            return AuthResult(
                success = false,
                error = "All fields are required"
            )
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return AuthResult(
                success = false,
                error = "Invalid email address"
            )
        }

        if (password.length < 6) {
            return AuthResult(
                success = false,
                error = "Password must be at least 6 characters"
            )
        }

        if (password != confirmPassword) {
            return AuthResult(
                success = false,
                error = "Passwords do not match"
            )
        }

        return AuthResult(success = true)
    }


    fun logout() {
        viewModelScope.launch {
            authRepo.logout()
            user = null
            authState = AuthState.LOGGED_OUT
        }
    }
}
