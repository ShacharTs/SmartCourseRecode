package com.smartcourse.data.repositories.user

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.storage.storage
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class StorageRepository @Inject constructor(
    private val client: SupabaseClient
) {
    suspend fun uploadUserAvatar(userId: String, imageBytes: ByteArray): String {
        val bucketName = "user_profile_image"
        val objectPath = "users/$userId.png"

        // Verify session
        client.auth.currentSessionOrNull() ?: throw IllegalStateException("User must be logged in")

        val bucket = client.storage.from(bucketName)
        bucket.upload(path = objectPath, data = imageBytes, upsert = true)

        // Fix: Ensure supabaseUrl has the https:// protocol
        var rawBase = client.supabaseUrl.trimEnd('/')
        if (!rawBase.startsWith("http")) {
            rawBase = "https://$rawBase"
        }

        // Return the full, formatted public URL
        return "$rawBase/storage/v1/object/public/$bucketName/$objectPath"
    }


}