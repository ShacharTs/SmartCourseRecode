package com.smartcourse.ui.screens.chat.state

data class LocationState(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
