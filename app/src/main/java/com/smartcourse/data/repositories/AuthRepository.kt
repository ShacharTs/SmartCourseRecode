package com.smartcourse.data.repositories

import android.content.Context
import com.smartcourse.auth.AuthResult
import com.smartcourse.auth.AuthStrategy
import com.smartcourse.data.models.usermodel.DomainUser
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val userRepo: UserRepository
) {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // ------------------------------------------------------------
    // LOGIN ENTRY (strategy handles provider)
    // ------------------------------------------------------------
    suspend fun loginWith(
        strategy: AuthStrategy,
        context: Context
    ): AuthResult {

        val result = strategy.login(context)

        if (result.success && result.userId != null) {
            loadOrCreateUser(result.userId)
        }

        return result
    }


    // ------------------------------------------------------------
    // USER PROFILE
    // ------------------------------------------------------------
    suspend fun loadOrCreateUser(userId: String): User {
        var profile = userRepo.loadUser(userId)

        if (profile == null) {
            val u = supabase.auth.currentUserOrNull()

            userRepo.createUser(
                id = userId,
                email = u?.email ?: "",
                name = u?.userMetadata?.get("full_name")?.toString() ?: "",
                image = u?.userMetadata?.get("avatar_url")?.toString() ?: "",
                bio = u?.userMetadata?.get("user_bio")?.toString() ?: "",
                role = UserRole.TEMP.name
            )

            profile = userRepo.loadUser(userId)
                ?: error("Failed to create user profile")
        }

        _currentUser.value = profile
        return profile
    }

    // ------------------------------------------------------------
    // SESSION RESTORE
    // ------------------------------------------------------------
    suspend fun restoreValidSession(): User? {
        val session = supabase.auth.currentSessionOrNull() ?: return null
        val userId = session.user?.id ?: return null
        return loadOrCreateUser(userId)
    }

    // ------------------------------------------------------------
    // LOGOUT
    // ------------------------------------------------------------
    suspend fun logout(strategy: AuthStrategy? = null) {
        strategy?.logout()
        supabase.auth.signOut()
        supabase.auth.clearSession()
        _currentUser.value = null
    }


    suspend fun registerWithEmail(
        email: String,
        password: String
    ) {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        // After login, load or create user profile
        val sessionUser = supabase.auth.currentSessionOrNull()?.user
            ?: error("Registration succeeded but no session user")

        loadOrCreateUser(sessionUser.id)
    }

    suspend fun toDomainUser(user: User): DomainUser? {
        return userRepo.toDomainUser(user)
    }


}
