@file:OptIn(ExperimentalMaterial3Api::class)

package com.smartcourse.ui.screens.chat.screen

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.screens.chat.controller.ChatController
import com.smartcourse.ui.screens.chat.launcher.CameraLauncher
import com.smartcourse.ui.screens.chat.launcher.GalleryLauncher
import com.smartcourse.ui.screens.chat.launcher.LocationLauncher
import com.smartcourse.ui.screens.chat.lifecycle.ChatLifecycle
import com.smartcourse.ui.screens.chat.ui.ChatScaffold
import com.smartcourse.ui.screens.chat.vm.CameraViewModel
import com.smartcourse.ui.screens.chat.vm.ChatViewModel
import com.smartcourse.ui.screens.chat.vm.GalleryViewModel
import com.smartcourse.ui.screens.chat.vm.LocationViewModel

@Composable
fun ChatScreen(
    navController: NavController,
    chatVM: ChatViewModel = hiltViewModel(),
    cameraVM: CameraViewModel = hiltViewModel(),
    galleryVM: GalleryViewModel = hiltViewModel(),
    locationVM: LocationViewModel = hiltViewModel(),
    myId: String,
    chatId: String
) {
    val context = LocalContext.current
    val messages by chatVM.messages.collectAsState()
    val listState = rememberLazyListState()

    var otherUser by remember { mutableStateOf<User?>(null) }
    var otherId by remember { mutableStateOf<String?>(null) }



    // auto scroll down
    ChatAutoScrollEffect(
        listState = listState,
        messagesCount = messages.size
    )


    //  launcher owns its own events
    CameraLauncher(context, cameraVM)
    GalleryLauncher(galleryVM)
    LocationLauncher(context, locationVM)

    //  Lifecycle only
    ChatLifecycle(
        chatVM = chatVM,
        chatId = chatId,
        myId = myId,
        onResolved = { id, user ->
            otherId = id
            otherUser = user
        }
    )

    //  State  side-effects only
    ChatController(
        chatVM = chatVM,
        cameraVM = cameraVM,
        galleryVM = galleryVM,
        locationVM = locationVM,
        chatId = chatId,
        myId = myId,
        resolveReceiver = { otherId }
    )

    //  UI only
    ChatScaffold(
        navController = navController,
        otherUser = otherUser,
        otherId = otherId,
        chatVM = chatVM,
        chatId = chatId,
        myId = myId,
        messages = messages,
        listState = listState,
        onCameraClick = { cameraVM.requestCamera() },
        onGalleryClick = { galleryVM.requestGallery() },
        onLocationClick = { locationVM.requestLocation() }
    )
}

@Composable
private fun ChatAutoScrollEffect(
    listState: androidx.compose.foundation.lazy.LazyListState,
    messagesCount: Int
) {
    var isAtBottom by remember { mutableStateOf(true) }

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset == 0
        }.collect { atBottom ->
            isAtBottom = atBottom
        }
    }

    LaunchedEffect(messagesCount) {
        if (isAtBottom && messagesCount > 0) {
            listState.scrollToItem(0)
        }
    }
}


