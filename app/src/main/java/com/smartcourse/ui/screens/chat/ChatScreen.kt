@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.smartcourse.ui.screens.chat

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.screens.chat.vm.CameraEvent
import com.smartcourse.ui.screens.chat.vm.CameraViewModel
import com.smartcourse.ui.screens.chat.vm.ChatViewModel
import com.smartcourse.ui.screens.chat.vm.GalleryEvent
import com.smartcourse.ui.screens.chat.vm.GalleryViewModel
import java.io.File

@Composable
fun ChatScreen(
    navController: NavController,
    chatVM: ChatViewModel = hiltViewModel(),
    myId: String,
    chatId: String
) {

    var otherUser by remember { mutableStateOf<User?>(null) }
    var otherId by remember { mutableStateOf<String?>(null) }

    val messages by chatVM.messages.collectAsState()
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    val context = LocalContext.current

    /* -------------------- GALLERY -------------------- */

    val galleryVM: GalleryViewModel = viewModel()

    val galleryLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                galleryVM.onImageSelected(uri)
            }
        }

    /* -------------------- CAMERA -------------------- */

    val cameraVM: CameraViewModel = viewModel()

    val tempPhotoUri = remember {
        val file = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                cameraVM.onPhotoCaptured(tempPhotoUri)
            } else {
                cameraVM.onError("Camera canceled")
            }
        }

    //  CAMERA PERMISSION LAUNCHER
    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                cameraLauncher.launch(tempPhotoUri)
            } else {
                cameraVM.onError("Camera permission denied")
            }
        }

    /* -------------------- EVENTS -------------------- */

    LaunchedEffect(Unit) {
        cameraVM.events.collect { event ->
            when (event) {
                CameraEvent.OpenCamera -> {
                    if (
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraLauncher.launch(tempPhotoUri)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            }
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

    /* -------------------- CHAT SETUP -------------------- */

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

    /* -------------------- UI -------------------- */

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
