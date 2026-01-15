@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.smartcourse.ui.screens.chat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.screens.chat.vm.ChatViewModel
import com.smartcourse.ui.screens.chat.vm.GalleryEvent
import com.smartcourse.ui.screens.chat.vm.GalleryViewModel

@Composable
fun ChatScreen(
    navController: NavController,
    chatVM: ChatViewModel,
    myId: String,
) {
    val chatId = chatVM.chatId
    var otherUser by remember { mutableStateOf<User?>(null) }
    var otherId by remember { mutableStateOf<String?>(null) }

    val messages by chatVM.messages.collectAsState()
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    val galleryVM: GalleryViewModel = viewModel()


    val galleryLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                galleryVM.onImageSelected(uri)
            }
        }

    LaunchedEffect(Unit) {
        galleryVM.events.collect { event ->
            when (event) {
                GalleryEvent.OpenGallery -> {
                    galleryLauncher.launch("image/*")
                }
            }
        }
    }




    LaunchedEffect(chatId) {
        chatVM.ensureFirebaseReady()
        otherId = chatVM.getReceiverId(chatId, myId)
        val (_, other) = resolveUsers(myId, chatVM.getBothUsers(chatId))
        otherUser = other
        chatVM.startListening(chatId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }


    DisposableEffect(chatId) {
        onDispose { chatVM.stopListening() }
    }

    ChatScaffold(
        navController = navController,
        otherUser = otherUser,
        otherId = otherId,
        chatVM = chatVM,
        chatId = chatId,
        myId = myId,
        messages = messages,
        listState = listState,
    )
}
