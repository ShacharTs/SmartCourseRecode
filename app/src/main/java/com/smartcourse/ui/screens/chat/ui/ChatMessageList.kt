package com.smartcourse.ui.screens.chat.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.smartcourse.data.models.chat.Message

@Composable
fun ChatMessageList(
    messages: List<Message>,
    myId: String,
    listState: LazyListState,
) {
    LazyColumn(
        state = listState,
        reverseLayout = true,
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = messages,
            key = { it.messageId }
        ) { msg ->
            MessageBubble(
                isMine = msg.senderId == myId,
                text = msg.text,
                type = msg.type // Pass the type so images render correctly
            )
        }
    }
}
