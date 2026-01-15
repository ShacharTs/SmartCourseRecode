package com.smartcourse.ui.screens.chat.state

import android.net.Uri

data class GalleryState(
    val selectedImage: Uri? = null,
    val error: String? = null
)
