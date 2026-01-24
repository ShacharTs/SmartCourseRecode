package com.smartcourse.ui.screens.chat.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.ui.screens.chat.state.LocationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LocationEvent {
    object GetLocation : LocationEvent()
}

@HiltViewModel
class LocationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(LocationState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<LocationEvent>()
    val events = _events.asSharedFlow()

    fun requestLocation() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null
            )
            _events.emit(LocationEvent.GetLocation)
        }
    }


    fun onLocationReceived(lat: Double, lng: Double) {
        _state.value = LocationState(latitude = lat, longitude = lng, isLoading = false)
    }

    fun onError(msg: String) {
        _state.value = _state.value.copy(
            isLoading = false,
            error = msg
        )
    }


    fun clear() {
        _state.value = LocationState()
    }
}
