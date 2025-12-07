package com.smartcourse.data.models.chat

import com.smartcourse.data.models.usermodel.User

data class ChatItem(
    val chatId: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastTimestamp: Long? = null,

    // for UI enrichment
    val otherUserId: String = "",
    val otherUser: User? = null
)
