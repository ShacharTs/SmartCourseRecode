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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.smartcourse.data.repositories.UserRepository
import com.smartcourse.navigation.RootNavigation
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.screens.setting.theme.ThemeViewModel
import com.smartcourse.ui.theme.SmartCourseTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    private val themeViewModel: ThemeViewModel by viewModels()
    // authViewModel is no longer needed here as it is provided via hiltViewModel() in the NavHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupNotifications()

        setContent {
            val themeMode by themeViewModel.themeMode.collectAsState()
            val navController = rememberNavController()

            // OPTIMIZATION: Handle External Intents (Notifications/Deep Links)
            // This observes the intent and triggers navigation if a CHAT_ID exists
            LaunchedEffect(intent) {
                intent.getStringExtra("CHAT_ID")?.let { chatId ->

                    // Replace with your actual route string
                    navController.navigate(Screen.ChatRoom.createRoute(chatId))

                    // Clear the extra so it doesn't trigger again on rotation
                    intent.removeExtra("CHAT_ID")
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                SmartCourseTheme(themeMode = themeMode) {
                    Surface(
                        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        RootNavigation(navController = navController)
                    }
                }
            }
        }
    }

    // OPTIMIZATION: Support notifications when the app is already in the background
    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Update the intent so LaunchedEffect sees the new data
    }

    private fun setupNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), 101)
            }
        }

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

