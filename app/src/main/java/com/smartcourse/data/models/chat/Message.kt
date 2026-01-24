package com.smartcourse.data.models.chat

/**
 * Firestore message model.
 *
 * Supabase user_id is used for sender/receiver identification.
 * Firebase handles storage and realtime delivery only.
 */
data class Message(
    val messageId : String = "",
    val chatId: String = "",
    val text: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val receiverId: String = "",
    val timestamp: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now(),
    val type: String = "text" // "text", "image", or "location"
)
