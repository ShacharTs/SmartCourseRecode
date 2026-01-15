package com.smartcourse.ui.screens.chat.state

import android.net.Uri

data class CameraState(
    val isLoading: Boolean = false,
    val photoUri: Uri? = null,
    val error: String? = null
)
