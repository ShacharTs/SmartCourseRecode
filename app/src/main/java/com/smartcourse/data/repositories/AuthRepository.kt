package com.smartcourse.data.repositories

import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val userRepo: UserRepository
) {

    suspend fun checkExistingSession(): User? {
        val session = supabase.auth.currentSessionOrNull() ?: return null
        val supaUser = session.user ?: return null
        return userRepo.loadUser(supaUser.id)
    }

    suspend fun loadOrCreateUser(userId: String): User {
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
                ?: throw IllegalStateException("Failed to create user profile")
        }

        return profile
    }

    // ------------------------------------------------------------
    // EMAIL LOGIN
    // ------------------------------------------------------------
    suspend fun loginEmail(email: String, pass: String): String? {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = pass
            }
            supabase.auth.currentUserOrNull()?.id
        } catch (e: Exception) {
            null
        }
    }

    // ------------------------------------------------------------
    // EMAIL REGISTER
    // ------------------------------------------------------------
    suspend fun registerEmail(email: String, pass: String): String? {
        return try {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = pass
            }
            supabase.auth.currentUserOrNull()?.id
        } catch (e: Exception) {
            null
        }
    }

    // ------------------------------------------------------------
    // GOOGLE LOGIN (moved from GoogleAuthStrategy)
    // ------------------------------------------------------------
    suspend fun loginGoogle(idToken: String, rawNonce: String): String? {
        return try {
            supabase.auth.signInWith(IDToken) {
                this.idToken = idToken
                this.nonce = rawNonce
                provider = Google
            }
            supabase.auth.currentUserOrNull()?.id
        } catch (e: Exception) {
            null
        }
    }

    suspend fun logout() {
        runCatching {
            supabase.auth.signOut()
            supabase.auth.clearSession()
        }
    }
}
