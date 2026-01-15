package com.smartcourse.ui.screens.chat.vm

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.smartcourse.ui.screens.chat.state.CameraState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CameraViewModel : ViewModel() {

    private val _state = MutableStateFlow(CameraState())
    val state = _state.asStateFlow()

    fun onPhotoCaptured(uri: Uri) {
        _state.value = CameraState(
            isLoading = false,
            photoUri = uri
        )
    }

    fun onCaptureStarted() {
        _state.value = CameraState(isLoading = true)
    }

    fun onError(msg: String) {
        _state.value = CameraState(error = msg)
    }

    fun clear() {
        _state.value = CameraState()
    }
}
