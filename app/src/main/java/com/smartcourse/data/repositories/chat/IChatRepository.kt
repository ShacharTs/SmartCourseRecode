package com.smartcourse.data.repositories.chat

import com.google.firebase.firestore.ListenerRegistration
import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.models.chat.Message

interface IChatRepository {
    fun createChatId(userId1: String, userId2: String): String
    suspend fun ensureChatExists(userA: String, userB: String): String
    suspend fun sendMessage(
        chatId: String,
        message: Message,
        myId: String,
        otherId: String,
        senderName: String
    )
    suspend fun getChatById(chatId: String): ChatItem
    fun listenToMessages(chatId: String, onMessages: (List<Message>) -> Unit): ListenerRegistration
    fun listenToUserChats(userId: String, onChats: (List<ChatItem>) -> Unit): ListenerRegistration
}