package com.smartcourse.data.repositories.user

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.smartcourse.data.remote.firebase.FirebaseClientProvider.firestore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val client: SupabaseClient
) {
    suspend fun updateFcmToken(token: String) {
        val userId = client.auth.currentUserOrNull()?.id ?: return
        firestore.collection("users").document(userId)
            .set(mapOf("fcmToken" to token), SetOptions.merge())
            .await()
    }

    suspend fun removeFcmToken() {
        val userId = client.auth.currentUserOrNull()?.id ?: return
        firestore.collection("users").document(userId)
            .update("fcmToken", FieldValue.delete())
            .await()
    }


    suspend fun ensureFcmTokenSaved() {
        val token = FirebaseMessaging.getInstance().token.await()
        updateFcmToken(token)
    }
}