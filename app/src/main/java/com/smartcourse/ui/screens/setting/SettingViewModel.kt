package com.smartcourse.ui.screens.setting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.smartcourse.data.repositories.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context,
    private val userRepository: UserRepository
) : ViewModel() {

    // Access local phone storage (SharedPreferences)
    private val prefs = context.getSharedPreferences("user_settings", android.content.Context.MODE_PRIVATE)

    // Load initial values directly from the phone memory
    var isChatEnabled by mutableStateOf(prefs.getBoolean("chat_enabled", true))
        private set

    var isAppNotificationsEnabled by mutableStateOf(prefs.getBoolean("notif_enabled", true))
        private set

    fun toggleChat(enabled: Boolean) {
        isChatEnabled = enabled
        // Save to phone memory
        prefs.edit().putBoolean("chat_enabled", enabled).apply()
    }

    fun toggleNotifications(enabled: Boolean) {
        isAppNotificationsEnabled = enabled
        // Save to phone memory
        prefs.edit().putBoolean("notif_enabled", enabled).apply()
    }
}