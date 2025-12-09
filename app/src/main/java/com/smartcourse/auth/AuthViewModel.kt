//package com.smartcourse.auth
//
//import android.content.Context
//import android.util.Log
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.credentials.CredentialManager
//import androidx.credentials.GetCredentialRequest
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.google.android.libraries.identity.googleid.GetGoogleIdOption
//import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
//import com.smartcourse.data.models.usermodel.User
//import com.smartcourse.data.models.usermodel.UserRole
//import com.smartcourse.data.repositories.AuthRepository
//import com.smartcourse.data.repositories.UserRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import io.github.jan.supabase.gotrue.auth
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.security.MessageDigest
//import java.util.UUID
//import javax.inject.Inject
//
//@HiltViewModel
//class AuthViewModel @Inject constructor(
//    private val authRepo: AuthRepository,
//    private val userRepo: UserRepository
//) : ViewModel() {
//
//    var authState by mutableStateOf(AuthState.LOGGED_OUT)
//        private set
//
//    var user by mutableStateOf<User?>(null)
//        private set
//
//    var signUpState = mutableStateOf<AuthResult?>(null)
//
//    // ------------------------------------------------------------
//    // INIT — FIXED FOR SUPABASE V3
//    // ------------------------------------------------------------
//    init {
//        viewModelScope.launch {
//            authState = AuthState.LOADING
//
//            val session = authRepo.client.auth.currentSessionOrNull()
//            if (session == null) {
//                authState = AuthState.LOGGED_OUT
//                return@launch
//            }
//
//            // Supabase v3: must always try refresh, refreshToken is not stored in session object
//            val refreshed = runCatching {
//                authRepo.client.auth.refreshCurrentSession()
//            }.isSuccess
//
//            if (!refreshed) {
//                authState = AuthState.LOGGED_OUT
//                return@launch
//            }
//
//            val sessionUser = authRepo.client.auth.currentUserOrNull()
//            if (sessionUser == null) {
//                authState = AuthState.LOGGED_OUT
//                return@launch
//            }
//
//            val profile = authRepo.loadOrCreateUser(sessionUser.id)
//            user = profile
//
//            authState =
//                if (profile.role == UserRole.TEMP) AuthState.REGISTERED
//                else AuthState.LOGGED_IN
//
//            Log.d("AuthViewModel", "INIT → Restored user ${profile.email}, role=${profile.role}")
//        }
//    }
//
//    // ------------------------------------------------------------
//    // EMAIL LOGIN
//    // ------------------------------------------------------------
//    suspend fun loginEmail(email: String, password: String): Boolean {
//        return withContext(Dispatchers.IO) {
//            try {
//                authState = AuthState.LOADING
//
//                val uid = authRepo.loginEmail(email, password)
//                if (uid == null) {
//                    authState = AuthState.LOGGED_OUT
//                    return@withContext false
//                }
//
//                val profile = authRepo.loadOrCreateUser(uid)
//                user = profile
//
//                authState =
//                    if (profile.role == UserRole.TEMP) AuthState.REGISTERED
//                    else AuthState.LOGGED_IN
//
//                Log.d("AuthViewModel", "loginEmail: ${profile.email}, state=${authState.name}")
//
//                return@withContext true
//            } catch (e: Exception) {
//                Log.e("AuthViewModel", "loginEmail ERROR", e)
//                authState = AuthState.LOGGED_OUT
//                return@withContext false
//            }
//        }
//    }
//
//    // ------------------------------------------------------------
//    // REGISTER
//    // ------------------------------------------------------------
//    suspend fun registerEmail(email: String, password: String): Boolean {
//        return try {
//            val uid = authRepo.registerEmail(email, password)
//
//            signUpState.value = AuthResult(uid != null, userId = uid)
//            uid != null
//        } catch (e: Exception) {
//            signUpState.value = AuthResult(false, error = e.localizedMessage)
//            false
//        }
//    }
//
//    // ------------------------------------------------------------
//    // GOOGLE — REQUEST ID TOKEN
//    // ------------------------------------------------------------
//    private suspend fun requestGoogleIdToken(context: Context): Pair<String, String>? {
//        return try {
//            val cm = CredentialManager.create(context)
//
//            val rawNonce = UUID.randomUUID().toString()
//            val hashed = MessageDigest.getInstance("SHA-256")
//                .digest(rawNonce.toByteArray())
//                .joinToString("") { "%02x".format(it) }
//
//            val googleOpt = GetGoogleIdOption.Builder()
//                .setFilterByAuthorizedAccounts(true)
//                .setAutoSelectEnabled(false)
//                .setServerClientId("332919763204-p0vp92hoab97p7brdtrr60cnjr9hidod.apps.googleusercontent.com")
//                .setNonce(hashed)
//                .build()
//
//            val request = GetCredentialRequest.Builder()
//                .addCredentialOption(googleOpt)
//                .build()
//
//            val result = cm.getCredential(context, request)
//            val cred = GoogleIdTokenCredential.createFrom(result.credential.data)
//
//            Pair(cred.idToken, rawNonce)
//        } catch (e: Exception) {
//            Log.e("AuthViewModel", "Google ID request failed", e)
//            null
//        }
//    }
//
//    // ------------------------------------------------------------
//    // GOOGLE LOGIN
//    // ------------------------------------------------------------
//    fun loginGoogle(context: Context) {
//        viewModelScope.launch {
//            authState = AuthState.LOADING
//
//            val (idToken, nonce) = requestGoogleIdToken(context) ?: run {
//                authState = AuthState.LOGGED_OUT
//                return@launch
//            }
//
//            val uid = authRepo.loginGoogle(idToken, nonce)
//            if (uid == null) {
//                authState = AuthState.LOGGED_OUT
//                return@launch
//            }
//
//            val profile = authRepo.loadOrCreateUser(uid)
//            user = profile
//
//            authState =
//                if (profile.role == UserRole.TEMP) AuthState.REGISTERED
//                else AuthState.LOGGED_IN
//        }
//    }
//
//    // ------------------------------------------------------------
//    // UPDATE ROLE
//    // ------------------------------------------------------------
//    fun updateUserRole(role: UserRole, onDone: (() -> Unit)? = null) {
//        viewModelScope.launch {
//            val u = user ?: return@launch
//            userRepo.updateUserRole(u.getUID(), role)
//            user = u.copy(role = role)
//            authState = AuthState.LOGGED_IN
//            onDone?.invoke()
//        }
//    }
//
//    // ------------------------------------------------------------
//    // REGISTRATION VALIDATION
//    // ------------------------------------------------------------
//    fun validateRegistration(
//        email: String,
//        password: String,
//        confirmPassword: String
//    ): AuthResult {
//
//        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank())
//            return AuthResult(false, "All fields are required")
//
//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
//            return AuthResult(false, "Invalid email address")
//
//        if (password.length < 6)
//            return AuthResult(false, "Password must be at least 6 characters")
//
//        if (password != confirmPassword)
//            return AuthResult(false, "Passwords do not match")
//
//        return AuthResult(true)
//    }
//
//    // ------------------------------------------------------------
//    // LOGOUT
//    // ------------------------------------------------------------
//    fun logout() {
//        viewModelScope.launch {
//            authRepo.logout()
//            user = null
//            authState = AuthState.LOGGED_OUT
//        }
//    }
//}


package com.smartcourse.auth

import android.content.Context
import android.util.Log
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
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository,
    val supabase: SupabaseClient
) : ViewModel() {
    var authState by mutableStateOf(AuthState.LOGGED_OUT)
        private set

    var user by mutableStateOf<User?>(null)
        private set




    var loginFailed by mutableStateOf(false)
        private set

    private val _signUpState = MutableStateFlow<AuthResult?>(null)
    val signUpState = _signUpState



    init {
        checkExistingSession()
    }

    fun setLoggedIn() {
        authState = AuthState.LOGGED_IN
    }

    fun setLoggedOut(){
        user = null
        authState = AuthState.LOGGED_OUT
    }

    fun setRegister(){
        authState = AuthState.REGISTERED
    }

    fun setLoading(){
        authState = AuthState.LOADING
    }


    private fun checkExistingSession() {
        viewModelScope.launch {
            try {
                val session = supabase.auth.currentSessionOrNull()

                // No session → logged out
                if (session == null) {
                    //authState = AuthState.LOGGED_OUT
                    setLoggedOut()
                    return@launch
                }

                val loggedUser = session.user
                if (loggedUser == null) {
                    //authState = AuthState.LOGGED_OUT
                    setLoggedOut()
                    return@launch
                }

                // Load SQL profile
                val profile = userRepo.loadUser(loggedUser.id)
                user = profile

                // Determine correct state
                authState = when (profile?.role) {
                    null,
                    UserRole.TEMP -> AuthState.REGISTERED

                    else -> AuthState.LOGGED_IN
                }

            } catch (e: Exception) {
                //authState = AuthState.LOGGED_OUT
                setLoggedOut()
            }
        }
    }


    suspend fun loadOrCreateUser(userId: String): User {
        // Try loading from SQL
        var profile = userRepo.loadUser(userId)

        if (profile == null) {
            val u = supabase.auth.currentUserOrNull()

            userRepo.createUser(
                id = userId,
                email = u?.email ?: "",
                name = u?.userMetadata?.get("full_name")?.toString() ?: "",
                image = u?.userMetadata?.get("avatar_url")?.toString() ?: "",
                role = UserRole.TEMP.name
            )


            profile = userRepo.loadUser(userId)
                ?: throw IllegalStateException("User creation failed")
        }

        return profile
    }

    fun login(strategy: AuthStrategy, context: Context) {
        viewModelScope.launch {
            //authState = AuthState.LOADING
            setLoading()

            val result = strategy.login(context = context)

            if (result.success && result.userId != null) {
                loginFailed = false

                // 1. Sync metadata
                userRepo.syncGoogleAvatar()

                // 2. Load SQL profile
                val loaded = loadOrCreateUser(result.userId)

                // 3. Assign user FIRST (important!)
                user = loaded

                // 4. Now user is safe, check role
                val role = loaded.getUserRole()

                authState = if (role == null ||  role == UserRole.TEMP) {
                    AuthState.REGISTERED
                } else {
                    AuthState.LOGGED_IN
                }


            } else {
                loginFailed = true
                //authState = AuthState.LOGGED_OUT
                setLoggedOut()
            }
        }
    }


    suspend fun loginWithResult(strategy: AuthStrategy, context: Context): Boolean {
        return try {
            val result = strategy.login(context = context)

            if (result.success && result.userId != null) {
                loginFailed = false

                // 1. Sync metadata
                userRepo.syncGoogleAvatar()

                // 2. Load SQL profile
                val loaded = loadOrCreateUser(result.userId)

                // 3. Assign user before state
                user = loaded

                // 4. Check role
                val role = loaded.getUserRole()

                authState = if (role == UserRole.TEMP) {
                    AuthState.REGISTERED
                } else {
                    AuthState.LOGGED_IN
                }

                // return false if TEMP (user needs to choose role)
                role != UserRole.TEMP

            } else {
                loginFailed = true
                //authState = AuthState.LOGGED_OUT
                setLoggedOut()
                false
            }

        } catch (e: Exception) {
            loginFailed = true
            //authState = AuthState.LOGGED_OUT
            setLoggedOut()
            false
        }
    }



    /**
     * Validate email + password inputs BEFORE signup.
     */
    fun validateRegistration(
        email: String,
        password: String,
        confirmPassword: String
    ): AuthResult {

        // 1. Check passwords match
        if (password != confirmPassword) {
            return AuthResult(
                success = false,
                error = "Passwords do not match"
            )
        }

        // 2. Validate email
        if (!isEmailStrict(email)) {
            return AuthResult(
                success = false,
                error = "Invalid email address"
            )
        }

        // 3. Validate password length
        if (password.length < 6) {
            return AuthResult(
                success = false,
                error = "Password must be at least 6 characters"
            )
        }

        return AuthResult(success = true)
    }


    fun registerAndLogin(email: String, pass: String) {
        viewModelScope.launch {
            try {
                // 1. Sign up
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = pass
                }

                // 2. Sign in
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = pass
                }

                // 3. Load user
                val sessionUser = supabase.auth.currentSessionOrNull()?.user
                if (sessionUser != null) {
                    user = loadOrCreateUser(sessionUser.id)
                }

                // 4. Move to REGISTERED
                authState = AuthState.REGISTERED

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    private fun isEmailStrict(email: String): Boolean {
        val regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(com|net|org|io|co\\.il)$")
        return regex.matches(email)
    }

    private fun extractSafeError(e: Exception): String {
        return try {
            e.localizedMessage
                ?.lineSequence()
                ?.firstOrNull()
                ?.trim()
                ?: "Unknown error"
        } catch (_: Exception) {
            "Unknown error"
        }
    }


    fun logout(strategy: AuthStrategy? = null) {
        viewModelScope.launch {

            // 1. Immediately navigate away (no flicker)
            //authState = AuthState.LOGGED_OUT
            setLoggedOut()

            // 2. Clear local state (prevents stale username)
            user = null
            //userId = null

            // 3. Perform external logout (Google)
            strategy?.logout()

            // 4. Perform server logout
            try {
                supabase.auth.signOut()
            } catch (_: Exception) {
            }

            // 5. Clear refresh tokens
            try {
                supabase.auth.clearSession()
            } catch (_: Exception) {
            }
        }
    }


    fun refreshUser(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            val sessionUser = supabase.auth.currentSessionOrNull()?.user
            if (sessionUser != null) {
                user = userRepo.loadUser(sessionUser.id)
            }
            onDone()
        }
    }



}