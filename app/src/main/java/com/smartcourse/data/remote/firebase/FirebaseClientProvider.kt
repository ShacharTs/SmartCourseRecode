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

    // Firebase Auth (used internally by FirebaseUserProvider)
    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    // Firestore (messages storage)
    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance().apply {
            firestoreSettings = firestoreSettings {
                isPersistenceEnabled = true
            }
        }
    }

    // Realtime DB (typing + presence)
    val realtime: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance().apply {
            setPersistenceEnabled(true)
        }
    }
}
