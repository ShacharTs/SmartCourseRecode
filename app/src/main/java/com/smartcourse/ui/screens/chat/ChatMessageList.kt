package com.smartcourse.ui.screens.chat

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.smartcourse.data.models.chat.Message

@Composable
fun ChatMessageList(
    messages: List<Message>,
    myId: String,
    listState: LazyListState,
) {
    LazyColumn(
        state = listState,
        reverseLayout = true
    ) {
        items(
            items = messages.asReversed(),
            key = { it.messageId }
        ) { msg ->
            MessageBubble(
                isMine = msg.senderId == myId,
                text = msg.text
            )
        }
    }
}

