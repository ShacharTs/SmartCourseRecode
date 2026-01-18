package com.smartcourse.core.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class AppLifecycleObserver : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        AppState.isInForeground = true
    }

    override fun onStop(owner: LifecycleOwner) {
        AppState.isInForeground = false
    }
}