@file:OptIn(ExperimentalMaterial3Api::class)

package com.smartcourse.ui.screens.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartcourse.data.models.chat.Message
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.screens.chat.vm.ChatViewModel
import com.smartcourse.ui.theme.LocalAppPalette

@Composable
fun ChatScaffold(
    navController: NavController,
    otherUser: User?,
    otherId: String?,
    chatVM: ChatViewModel,
    chatId: String,
    myId: String,
    messages: List<Message>,
    listState: LazyListState,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onLocationClick: () -> Unit
) {
    var input by rememberSaveable { mutableStateOf("") }
    var showAttachSheet by remember { mutableStateOf(false) }

    val chat = LocalAppPalette.current.chatRoom

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .background(Brush.verticalGradient(chat.backgroundGradient))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = { ChatTopBar(navController, otherUser) },
            bottomBar = {
                ChatInputBar(
                    input = input,
                    onInputChange = { input = it },
                    onSend = {
                        val receiver = otherId ?: return@ChatInputBar
                        if (input.isBlank()) return@ChatInputBar
                        chatVM.sendMessage(chatId, input, myId, receiver)
                        input = ""
                    },
                    onAttachClick = { showAttachSheet = true }
                )
            }
        ) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                shape = RoundedCornerShape(24.dp),
                color = chat.card.copy(alpha = 0.92f)
            ) {
                ChatMessageList(messages, myId, listState)
            }
        }

        if (showAttachSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAttachSheet = false }
            ) {
                AttachSheetContent(
                    onCamera = {
                        onCameraClick()
                        showAttachSheet = false
                    },
                    onGallery = {
                        onGalleryClick()
                        showAttachSheet = false
                    },
                    onLocation = {
                        onLocationClick()
                        showAttachSheet = false
                    }
                )
            }
        }
    }
}
