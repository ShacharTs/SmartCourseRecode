package com.smartcourse.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.createFrom
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import java.security.MessageDigest
import java.util.UUID

/**
 * Connecting to Google Auth Provider using Credential Manager
 */
class GoogleAuthStrategy(
    private val supabase: SupabaseClient
) : AuthStrategy {

    override suspend fun login(context: Context): AuthResult {
        return try {
            val cm = CredentialManager.create(context)

            val rawNonce = UUID.randomUUID().toString()
            val hashed = MessageDigest.getInstance("SHA-256")
                .digest(rawNonce.toByteArray())
                .joinToString("") { "%02x".format(it) }

            val googleWebClientId = "332919763204-p0vp92hoab97p7brdtrr60cnjr9hidod.apps.googleusercontent.com"

            val googleOpt = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setServerClientId(googleWebClientId)
                .setNonce(hashed)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleOpt)
                .build()

            val result = cm.getCredential(context, request)
            val cred = createFrom(result.credential.data)

            supabase.auth.signInWith(IDToken) {
                idToken = cred.idToken
                nonce = rawNonce
                provider = Google
            }

            val loggedUser = supabase.auth.currentUserOrNull()

            AuthResult(
                success = loggedUser != null,
                userId = loggedUser?.id
            )

        } catch (e: Exception) {
            AuthResult(false, error = e.message)
        }
    }

    override suspend fun logout() {
        supabase.auth.signOut()
    }
}
