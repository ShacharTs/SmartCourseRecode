package com.smartcourse.ui.screens.chat.launcher

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.smartcourse.ui.screens.chat.vm.GalleryEvent
import com.smartcourse.ui.screens.chat.vm.GalleryViewModel

@Composable
fun GalleryLauncher(
    galleryVM: GalleryViewModel
) {
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { galleryVM.onImageSelected(it) }
        }

    LaunchedEffect(Unit) {
        galleryVM.events.collect { event ->
            if (event is GalleryEvent.OpenGallery) {
                galleryLauncher.launch("image/*")
            }
        }
    }
}
