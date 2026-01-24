package com.smartcourse.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.smartcourse.MainActivity
import com.smartcourse.R
import com.smartcourse.core.lifecycle.AppState
import com.smartcourse.data.repositories.user.NotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {



    @Inject
    lateinit var notificationRepo: NotificationRepository

    /* ------------------------------------------------------------
     * Notification type constants (NO magic strings)
     * ---------------------------------------------------------- */
    object NotificationTypes {
        const val CHAT = "CHAT"
        const val SYSTEM = "SYSTEM"
        const val REMINDER = "REMINDER"
        const val FIREBASE_EVENT = "FIREBASE_EVENT"
    }

    /* ------------------------------------------------------------
     * Token handling
     * ---------------------------------------------------------- */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            notificationRepo.updateFcmToken(token)
        }
    }

    /* ------------------------------------------------------------
     * Message entry point
     * ---------------------------------------------------------- */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val prefs = getSharedPreferences("user_settings", MODE_PRIVATE)
        if (!prefs.getBoolean("notif_enabled", true)) return

        val data = remoteMessage.data
        val type = data["type"] ?: return

        when (type) {
            NotificationTypes.CHAT -> handleChatNotification(data, prefs)
            NotificationTypes.SYSTEM -> handleSystemNotification(data)
            NotificationTypes.REMINDER -> handleReminderNotification(data)
            NotificationTypes.FIREBASE_EVENT -> handleFirebaseEventNotification(data)
        }
    }

    /* ------------------------------------------------------------
     * Handlers (ONE per type)
     * ---------------------------------------------------------- */

    private fun handleChatNotification(
        data: Map<String, String>,
        prefs: android.content.SharedPreferences
    ) {
        if (!prefs.getBoolean("chat_enabled", true)) return

        val chatId = data["chatId"] ?: return
        val senderName = data["senderName"]
        val message = data["text"] ?: "New message"

        if (AppState.isInForeground) return

        showNotification(
            channelId = Channels.CHAT,
            title = senderName?.let { "New message from $it" } ?: "New message",
            message = message,
            action = NotificationTypes.CHAT,
            id = chatId
        )
    }

    private fun handleSystemNotification(data: Map<String, String>) {
        val message = data["text"] ?: return
        showNotification(
            channelId = Channels.SYSTEM,
            title = "System",
            message = message,
            action = NotificationTypes.SYSTEM,
            id = "system"
        )
    }

    private fun handleReminderNotification(data: Map<String, String>) {
        val message = data["text"] ?: return
        showNotification(
            channelId = Channels.REMINDER,
            title = "Reminder",
            message = message,
            action = NotificationTypes.REMINDER,
            id = "reminder"
        )
    }

    private fun handleFirebaseEventNotification(data: Map<String, String>) {
        val title = data["title"] ?: "Update"
        val message = data["text"] ?: return
        val screen = data["screen"] ?: "HOME"

        showNotification(
            channelId = Channels.SYSTEM,
            title = title,
            message = message,
            action = NotificationTypes.FIREBASE_EVENT,
            id = screen
        )
    }

    /* ------------------------------------------------------------
     * Notification infrastructure
     * ---------------------------------------------------------- */

    private fun showNotification(
        channelId: String,
        title: String,
        message: String,
        action: String,
        id: String
    ) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createChannelIfNeeded(manager, channelId)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("NOTIFICATION_ACTION", action)
            when(action){
                NotificationTypes.CHAT -> {
                    putExtra("CHAT_ID", id)
                }else ->{
                putExtra("NOTIFICATION_ID", id)
                }
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(id.hashCode(), notification)
    }

    /* ------------------------------------------------------------
     * Channels (separated per type)
     * ---------------------------------------------------------- */
    object Channels {
        const val CHAT = "chat_notifications"
        const val SYSTEM = "system_notifications"
        const val REMINDER = "reminder_notifications"
    }

    private fun createChannelIfNeeded(
        manager: NotificationManager,
        channelId: String
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        if (manager.getNotificationChannel(channelId) != null) return

        val (name, importance) = when (channelId) {
            Channels.CHAT -> "Chat Messages" to NotificationManager.IMPORTANCE_HIGH
            Channels.SYSTEM -> "System Updates" to NotificationManager.IMPORTANCE_DEFAULT
            Channels.REMINDER -> "Reminders" to NotificationManager.IMPORTANCE_HIGH
            else -> "General" to NotificationManager.IMPORTANCE_DEFAULT
        }

        manager.createNotificationChannel(
            NotificationChannel(channelId, name, importance)
        )
    }
}
