package com.smartcourse

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.FirebaseApp
import com.smartcourse.core.lifecycle.AppLifecycleObserver
import com.smartcourse.data.remote.supbase.SupabaseClientProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartCourseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get()
            .lifecycle
            .addObserver(AppLifecycleObserver())
        SupabaseClientProvider.init(this)
        FirebaseApp.initializeApp(this)


    }
}