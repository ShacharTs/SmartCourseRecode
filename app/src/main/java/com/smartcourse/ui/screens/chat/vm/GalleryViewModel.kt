package com.smartcourse.ui.screens.chat.vm

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.ui.screens.chat.state.GalleryState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class GalleryEvent {
    object OpenGallery : GalleryEvent()
}

class GalleryViewModel : ViewModel() {

    private val _state = MutableStateFlow(GalleryState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<GalleryEvent>()
    val events = _events.asSharedFlow()

    fun requestGallery() {
        viewModelScope.launch {
            _events.emit(GalleryEvent.OpenGallery)
        }
    }

    fun onImageSelected(uri: Uri) {
        _state.value = _state.value.copy(selectedImage = uri)
    }



    fun clear() {
        _state.value = GalleryState()
    }
}
