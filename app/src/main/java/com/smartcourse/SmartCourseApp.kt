package com.smartcourse

import android.app.Application
import com.google.firebase.FirebaseApp
import com.smartcourse.data.remote.supbase.SupabaseClientProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartCourseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        SupabaseClientProvider.init(this)
        FirebaseApp.initializeApp(this)


    }
}
