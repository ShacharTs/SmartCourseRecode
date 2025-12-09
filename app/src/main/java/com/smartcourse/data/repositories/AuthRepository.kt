package com.smartcourse.data.repositories

import com.smartcourse.auth.AuthResult
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val userRepo: UserRepository
) {

    // ------------------------------------------------------------
    // CHECK EXISTING SESSION
    // ------------------------------------------------------------
    suspend fun checkExistingSession(): User? {
        val session = supabase.auth.currentSessionOrNull() ?: return null
        val supaUser = session.user ?: return null

        return userRepo.loadUser(supaUser.id)
    }

    // ------------------------------------------------------------
    // LOAD OR CREATE USER (OLD VM NEEDS THIS)
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
                role = UserRole.TEMP.name
            )

            profile = userRepo.loadUser(userId)
                ?: throw IllegalStateException("Failed to create user profile")
        }

        return profile
    }

    // ------------------------------------------------------------
    // GENERIC REGISTER FOR OLD VM
    // ------------------------------------------------------------
    suspend fun register(email: String, password: String): AuthResult {
        return try {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            AuthResult(success = true)
        } catch (e: Exception) {
            AuthResult(false, e.message ?: "Registration failed")
        }
    }

    // ------------------------------------------------------------
    // GENERIC LOGIN FOR OLD VM
    // ------------------------------------------------------------
    suspend fun login(email: String, password: String): AuthResult {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            // Fetch the authenticated user *after* login
            val u = supabase.auth.currentUserOrNull()
            val uid = u?.id

            AuthResult(uid != null, userId = uid)

        } catch (e: Exception) {
            AuthResult(false, e.message ?: "Login failed")
        }
    }


    // ------------------------------------------------------------
    // SIMPLE EMAIL LOGIN (NEW VM USES THIS)
    // ------------------------------------------------------------
    suspend fun loginEmail(email: String, pass: String): Boolean {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = pass
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    // ------------------------------------------------------------
    // SIMPLE EMAIL REGISTER (NEW VM USES THIS)
    // ------------------------------------------------------------
    suspend fun registerEmail(email: String, pass: String): Boolean {
        return try {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = pass
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    // ------------------------------------------------------------
    // SYNC GOOGLE AVATAR (OLD VM NEEDS IT)
    // ------------------------------------------------------------
    suspend fun syncGoogleAvatar() {
        val u = supabase.auth.currentUserOrNull() ?: return
        val avatar = u.userMetadata?.get("avatar_url")?.toString() ?: return
        userRepo.updateUserImage(u.id, avatar)
    }

    // ------------------------------------------------------------
    // LOGOUT
    // ------------------------------------------------------------
    suspend fun logout() {
        try {
            supabase.auth.signOut()
            supabase.auth.clearSession()
        } catch (_: Exception) { }
    }
}
