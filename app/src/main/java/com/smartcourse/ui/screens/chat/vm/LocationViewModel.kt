package com.smartcourse.ui.screens.chat.vm

import androidx.lifecycle.ViewModel
import com.smartcourse.ui.screens.chat.state.LocationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationViewModel : ViewModel() {

    private val _state = MutableStateFlow(LocationState())
    val state = _state.asStateFlow()

    fun onLocationRequest() {
        _state.value = LocationState(isLoading = true)
    }

    fun onLocationReceived(lat: Double, lng: Double) {
        _state.value = LocationState(
            latitude = lat,
            longitude = lng
        )
    }

    fun onError(msg: String) {
        _state.value = LocationState(error = msg)
    }

    fun clear() {
        _state.value = LocationState()
    }
}
