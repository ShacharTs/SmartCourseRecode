@file:Suppress("DEPRECATION")

package com.smartcourse.di

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseApp(): FirebaseApp {
        return FirebaseApp.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(app: FirebaseApp): FirebaseFirestore {
        return FirebaseFirestore.getInstance(app).apply {
            firestoreSettings = firestoreSettings {
                isPersistenceEnabled = true
            }
        }
    }
}
