package com.smartcourse.auth


import android.content.Context
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email

class EmailAuthStrategy(
    private val email: String,
    private val password: String,
    private val supabase: SupabaseClient
) : AuthStrategy {

    override suspend fun login(context: Context): AuthResult {
        return try {

            // Perform login (this stores session internally)
            supabase.auth.signInWith(Email) {
                email = this@EmailAuthStrategy.email
                password = this@EmailAuthStrategy.password
            }

            // Retrieve the now-updated session
            val session = supabase.auth.currentSessionOrNull()
            val user = session?.user

            AuthResult(
                success = user != null,
                userId = user?.id,
            )

        } catch (e: Exception) {
            AuthResult(
                success = false,
                error = mapAuthError(e.message)
            )
        }
    }


    override suspend fun logout() {
        supabase.auth.signOut()
    }

    private fun mapAuthError(raw: String?): String {
        if (raw == null) return "Login failed"

        return when {
            raw.contains("invalid login credentials", true) ->
                "Invalid email or password"

            raw.contains("missing email", true) ->
                "Email is required"

            raw.contains("email not confirmed", true) ->
                "Please confirm your email first"

            else ->
                "Login failed"
        }
    }

}
