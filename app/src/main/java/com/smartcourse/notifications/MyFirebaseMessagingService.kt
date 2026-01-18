package com.smartcourse.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.smartcourse.MainActivity
import com.smartcourse.R
import com.smartcourse.core.lifecycle.AppState
import com.smartcourse.data.repositories.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var userRepository: UserRepository

    /**
     * Called when a new FCM token is generated for the device.
     * This token is required to send push notifications to this specific device.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("FCM_TOKEN", "onNewToken called. token=$token")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                userRepository.updateFcmToken(token)
                Log.d("FCM_TOKEN", "Token successfully saved to backend")
            } catch (e: Exception) {
                Log.e("FCM_TOKEN", "Failed to save token", e)
            }
        }
    }


//    /**
//     * Called when a message is received from Firebase Cloud Messaging (FCM).
//     */
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        val chatId = remoteMessage.data["chatId"] ?: return
//
//        FirebaseFirestore.getInstance()
//            .collection("chats")
//            .document(chatId)
//            .update(
//                "updated_at", Timestamp.now()
//            )
//    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCM_DEBUG", "DATA = ${remoteMessage.data}")
        Log.d("FCM_DEBUG", "NOTIFICATION = ${remoteMessage.notification}")

        val data = remoteMessage.data
        val chatId = data["chatId"] ?: return
        val senderName = data["senderName"]

        Log.d("FCM_DEBUG", "senderName = $senderName")

        // In your Repository, the message field is called "text"
        // We check both "text" and "body" for safety
        val messageBody = data["text"] ?: data["body"] ?: "New message received"

        if (AppState.isInForeground) {
            handleInAppMessage(chatId, data)
            return
        }

        // Build the notification title using the sender's name
        val title = if (!senderName.isNullOrBlank()) {
            Log.d("test",senderName)
            "New message from $senderName"
        } else {
            "New message "
        }

        showNotification(title, messageBody, chatId)
    }



    private fun handleInAppMessage(
        chatId: String,
        data: Map<String, String>
    ) {
        // Intentionally left blank.
        // Firestore listeners will update the UI automatically.
    }





    /**
     * Builds and displays a system notification.
     */
    private fun showNotification(
        title: String,
        message: String,
        chatId: String
    ) {
        val channelId = "chat_notifications"
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("NOTIFICATION_ACTION", "CHAT_MESSAGE")
            putExtra("CHAT_ID", chatId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            chatId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // CRITICAL
            .build()

        notificationManager.notify(chatId.hashCode(), notification)
    }


    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "chat_notifications", // Must match your channelId
                "Chat Messages",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new chat messages"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

}