import { onDocumentCreated } from "firebase-functions/v2/firestore";
import * as admin from "firebase-admin";

admin.initializeApp();

export const onMessageCreated = onDocumentCreated("messages/{chatId}/msgs/{msgId}", async (event) => {
    const snap = event.data;
    if (!snap) {
        console.log("No data found in the event.");
        return null;
    }

    const messageData = snap.data();
    const chatId = event.params.chatId;

    // 1. Get message details
    // Ensure your app saves 'receiverId' and 'senderName' inside the message document
    const senderName = messageData.senderName || "New Message";
    const text = messageData.text || "You have a new message";
    const receiverId = messageData.receiverId;

    if (!receiverId) {
        console.error("No receiverId found in the message document.");
        return null;
    }

    try {
        // 2. Fetch the receiver's FCM token from your 'users' collection
        const userDoc = await admin.firestore().collection("users").doc(receiverId).get();
        const userData = userDoc.data();
        const fcmToken = userData?.fcmToken;

        if (!fcmToken) {
            console.log(`User ${receiverId} does not have a registered FCM token.`);
            return null;
        }

        // 3. Build the payload to match your Android MyFirebaseMessagingService.kt logic
        const payload = {
            token: fcmToken,
            data: {
                chatId: chatId,
                senderName: senderName,
                text: text,
            },
        };

        // 4. Send the notification
        const response = await admin.messaging().send(payload);
        console.log("Successfully sent message:", response);
    } catch (error) {
        console.error("Error sending notification:", error);
    }

    return null;
});