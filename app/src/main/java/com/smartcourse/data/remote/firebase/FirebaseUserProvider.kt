package com.smartcourse.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Provides the Firebase authentication context required for Firestore
 * and Realtime Database access.
 *
 * This authentication layer is strictly internal: it does not represent
 * the application's real user identity (managed by Supabase).
 */
object FirebaseUserProvider {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    /**
     * Ensures the app has an authenticated Firebase session.
     * If none exists, an anonymous session is created.
     *
     * Returns the Firebase-assigned UID for this session.
     */
    suspend fun ensureFirebaseUser(): String {
        val current = auth.currentUser
        if (current != null) {
            return current.uid
        }

        val result = auth.signInAnonymously().await()
        return result.user!!.uid
    }


}
