import { onDocumentCreated } from "firebase-functions/v2/firestore";
import * as admin from "firebase-admin";

admin.initializeApp();

/* ------------------------------------------------------------------
 * Notification contract (shared idea with Android)
 * ---------------------------------------------------------------- */

const NotificationTypes = {
  CHAT: "CHAT",
  SYSTEM: "SYSTEM",
  REMINDER: "REMINDER",
  FIREBASE_EVENT: "FIREBASE_EVENT",
} as const;

type NotificationType =
  typeof NotificationTypes[keyof typeof NotificationTypes];

/* ------------------------------------------------------------------
 * Helper: build FCM payload
 * ---------------------------------------------------------------- */

function buildPayload(
  token: string,
  type: NotificationType,
  data: Record<string, string>
) {
  return {
    token,
    data: {
      v: "1",          // payload version (future-proof)
      type,            // REQUIRED
      ...data,
    },
  };
}

/* ------------------------------------------------------------------
 * Chat message notification
 * Triggered when a new message is created
 * ---------------------------------------------------------------- */

export const onMessageCreated = onDocumentCreated(
  "messages/{chatId}/msgs/{msgId}",
  async (event) => {

    const snap = event.data;
    if (!snap) return null;

    const messageData = snap.data();
    const chatId = event.params.chatId;

    const receiverId = messageData.receiverId;
    if (!receiverId) return null;

    const senderName = messageData.senderName || "New message";
    const text = messageData.text || "You have a new message";

    // Fetch receiver FCM token
    const userDoc = await admin
      .firestore()
      .collection("users")
      .doc(receiverId)
      .get();

    const fcmToken = userDoc.data()?.fcmToken;
    if (!fcmToken) return null;

    const payload = buildPayload(
      fcmToken,
      NotificationTypes.CHAT,
      {
        chatId,
        senderName,
        text,
      }
    );

    await admin.messaging().send(payload);
    return null;
  }
);

/* ------------------------------------------------------------------
 * Example: System notification (manual / future use)
 * ---------------------------------------------------------------- */

export async function sendSystemNotification(
  userId: string,
  message: string
) {
  const userDoc = await admin.firestore().collection("users").doc(userId).get();
  const fcmToken = userDoc.data()?.fcmToken;
  if (!fcmToken) return;

  const payload = buildPayload(
    fcmToken,
    NotificationTypes.SYSTEM,
    {
      title: "System",
      text: message,
    }
  );

  await admin.messaging().send(payload);
}

/* ------------------------------------------------------------------
 * Example: Firebase event notification (approval, role change, etc.)
 * ---------------------------------------------------------------- */

export async function sendFirebaseEventNotification(
  userId: string,
  title: string,
  text: string,
  screen: string
) {
  const userDoc = await admin.firestore().collection("users").doc(userId).get();
  const fcmToken = userDoc.data()?.fcmToken;
  if (!fcmToken) return;

  const payload = buildPayload(
    fcmToken,
    NotificationTypes.FIREBASE_EVENT,
    {
      title,
      text,
      screen, // Android can navigate based on this
    }
  );

  await admin.messaging().send(payload);
}
