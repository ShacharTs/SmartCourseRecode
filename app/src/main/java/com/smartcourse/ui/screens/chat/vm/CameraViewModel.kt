package com.smartcourse.ui.screens.chat.vm

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.ui.screens.chat.state.CameraState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CameraEvent {
    object OpenCamera : CameraEvent()
}

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(CameraState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<CameraEvent>()
    val events = _events.asSharedFlow()

    fun requestCamera() {
        viewModelScope.launch {
            _events.emit(CameraEvent.OpenCamera)
        }
    }

    fun onPhotoCaptured(uri: Uri) {
        _state.value = CameraState(isLoading = false, photoUri = uri)
        // Here you would eventually call a function to upload to Firebase/Supabase
    }

    fun onError(msg: String) {
        _state.value = CameraState(error = msg)
    }

    fun clear() {
        _state.value = CameraState()
    }
}
