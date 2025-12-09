package com.smartcourse

import android.app.Application
import com.smartcourse.data.remote.supbase.SupabaseClientProvider
import com.smartcourse.di.SupabaseModule
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartCourseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        SupabaseClientProvider.init(this)
    }
}
