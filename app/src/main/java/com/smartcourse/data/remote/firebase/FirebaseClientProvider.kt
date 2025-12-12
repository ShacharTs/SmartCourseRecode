@file:Suppress("DEPRECATION")

package com.smartcourse.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestoreSettings

/**
 * Provides Firebase service instances (Firestore, Realtime DB, Auth).
 * Centralized access point for all Firebase-related operations.
 */
object FirebaseClientProvider {

    // Use lazy, but ensure it's ONLY accessed after the app is fully set up.
    // However, since Hilt calls this, we must trust the Application's onCreate.

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    // Use a regular lazy block, assuming SmartCourseApp.onCreate has executed completely.
    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance().apply {
            // Check if this is the cause of the crash (sometimes settings are applied too early)
            firestoreSettings = firestoreSettings {
                isPersistenceEnabled = true
            }
        }
    }

    val realtime: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance().apply {
            setPersistenceEnabled(true)
        }
    }
}
