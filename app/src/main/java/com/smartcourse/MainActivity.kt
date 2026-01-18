package com.smartcourse

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.repositories.UserRepository
import com.smartcourse.navigation.RootNavigation
import com.smartcourse.ui.screens.setting.theme.ThemeViewModel
import com.smartcourse.ui.theme.SmartCourseTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    private val authViewModel: AuthViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize notification setup
        setupNotifications()

        // Check if the app was opened from a notification
        val chatIdFromNotification = intent.getStringExtra("CHAT_ID")
//        if (chatIdFromNotification != null) {
//            // We have a chatId! Later you can use this to navigate:
//            // navController.navigate("chat_screen/$chatIdFromNotification")
//        }

        setContent {
            val themeMode by themeViewModel.themeMode.collectAsState()

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                SmartCourseTheme(themeMode = themeMode) {
                    Surface(
                        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        RootNavigation(
                            authViewModel = authViewModel
                        )
                    }
                }
            }
        }

    }

    private fun setupNotifications() {
        // Request permissions for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), 101)
            }
        }

        // Fetch and save the FCM token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                lifecycleScope.launch {
                    try {
                        userRepository.updateFcmToken(token)
                    } catch (e: Exception) {
                        // Fail silently
                    }
                }
            }
        }
    }


}

