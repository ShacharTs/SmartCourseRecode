package com.smartcourse.ui.screens.chat.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.smartcourse.ui.screens.chat.vm.CameraViewModel
import com.smartcourse.ui.screens.chat.vm.ChatViewModel
import com.smartcourse.ui.screens.chat.vm.GalleryViewModel
import com.smartcourse.ui.screens.chat.vm.LocationViewModel


@Composable
fun ChatController(
    chatVM: ChatViewModel,
    cameraVM: CameraViewModel,
    galleryVM: GalleryViewModel,
    locationVM: LocationViewModel,
    chatId: String,
    myId: String,
    resolveReceiver: () -> String?
) {
    val cameraState by cameraVM.state.collectAsState()
    val galleryState by galleryVM.state.collectAsState()
    val locationState by locationVM.state.collectAsState()

    LaunchedEffect(cameraState.photoUri) {
        cameraState.photoUri?.let {
            chatVM.sendImageMessage(chatId, it, myId, resolveReceiver() ?: return@let)
            cameraVM.clear()
        }
    }

    LaunchedEffect(galleryState.selectedImage) {
        galleryState.selectedImage?.let {
            chatVM.sendImageMessage(chatId, it, myId, resolveReceiver() ?: return@let)
            galleryVM.clear()
        }
    }

    LaunchedEffect(locationState.latitude, locationState.longitude) {
        if (locationState.latitude != 0.0 && locationState.longitude != 0.0) {
            chatVM.sendLocationMessage(
                chatId,
                locationState.latitude,
                locationState.longitude,
                myId,
                resolveReceiver() ?: return@LaunchedEffect
            )
        }
    }
}

