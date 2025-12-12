package com.smartcourse.data.models.chat

import com.google.firebase.Timestamp

/**
 * Firestore message model.
 *
 * Supabase user_id is used for sender/receiver identification.
 * Firebase handles storage and realtime delivery only.
 */
data class Message(
    val messageId: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val type: String = "text",
)
