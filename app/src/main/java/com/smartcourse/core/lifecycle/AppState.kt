package com.smartcourse.core.lifecycle

object AppState {
    @Volatile
    var isInForeground: Boolean = false
}