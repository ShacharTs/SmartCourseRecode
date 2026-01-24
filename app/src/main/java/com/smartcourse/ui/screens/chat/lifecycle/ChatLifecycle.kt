package com.smartcourse.ui.screens.chat.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.screens.chat.util.resolveUsers
import com.smartcourse.ui.screens.chat.vm.ChatViewModel


@Composable
fun ChatLifecycle(
    chatVM: ChatViewModel,
    chatId: String,
    myId: String,
    onResolved: (String, User?) -> Unit
) {
    LaunchedEffect(chatId) {
        chatVM.ensureFirebaseReady()
        chatVM.startListening(chatId)

        val receiverId = chatVM.getReceiverId(chatId, myId)
        val users = chatVM.getBothUsers(chatId)
        val (_, other) = resolveUsers(myId, users)

        onResolved(receiverId, other)
    }

    DisposableEffect(chatId) {
        onDispose { chatVM.stopListening() }
    }
}
