package com.smartcourse.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
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

        // Read from DATA payload only
        val data = remoteMessage.data
        val chatId = data["chatId"] ?: return

        // Update Firestore to trigger chat list listeners
        FirebaseFirestore.getInstance()
            .collection("chats")
            .document(chatId)
            .update("updated_at", Timestamp.now())

        // Build notification content locally (NO remoteMessage.notification)
        val title = data["title"] ?: "New message"
        val body = data["body"] ?: "You have a new message"

        showNotification(title, body, chatId)
    }





    /**
     * Builds and displays a system notification.
     */
    private fun showNotification(title: String, message: String, chatId: String?) {
        val channelId = "chat_notifications"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        createNotificationChannel(notificationManager)

        // Intent to open MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            // Pass the chatId so MainActivity can navigate to the right screen
            putExtra("CHAT_ID", chatId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
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