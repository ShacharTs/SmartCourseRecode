package com.smartcourse.data.repositories.chat

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.models.chat.Message
import com.smartcourse.data.repositories.DbTable
import com.smartcourse.data.repositories.user.ProfileRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val profileRepo: ProfileRepository
) : IChatRepository {

    override fun createChatId(userId1: String, userId2: String): String {
        return listOf(userId1, userId2).sorted().joinToString("_")
    }

    override suspend fun ensureChatExists(userA: String, userB: String): String {
        val chatId = createChatId(userA, userB)
        val ref = firestore.collection(DbTable.CHATS).document(chatId)

        val snap = ref.get().await()
        if (!snap.exists()) {
            ref.set(
                mapOf(
                    DbTable.PARTICIPANTS to listOf(userA, userB),
                    DbTable.LAST_MESSAGE to "",
                    DbTable.UPDATED_AT to Timestamp.now()
                )
            ).await()
        }
        return chatId
    }

    override suspend fun sendMessage(
        chatId: String,
        message: Message,
        myId: String,
        otherId: String,
        senderName: String
    ) {
        val currentUser = profileRepo.loadUser(myId)
        val myName = currentUser?.displayName ?: "Unknown"

        val messageWithDetails = message.copy(
            chatId = chatId,
            senderId = myId,
            senderName = myName,
            receiverId = otherId,
            timestamp = Timestamp.now()
        )

        try {
            firestore.collection(DbTable.MESSAGES)
                .document(chatId)
                .collection(DbTable.MSGS)
                .add(messageWithDetails)
                .await()

            updateChatMetadata(chatId, messageWithDetails.text)
        } catch (e: Exception) {
            Log.e("ChatRepository", "CRITICAL ERROR in sendMessage: ${e.message}")
            throw e
        }
    }

    private suspend fun updateChatMetadata(chatId: String, lastMessage: String) {
        try {
            firestore.collection(DbTable.CHATS)
                .document(chatId)
                .set(
                    mapOf(
                        DbTable.LAST_MESSAGE to lastMessage,
                        DbTable.UPDATED_AT to Timestamp.now()
                    ),
                    SetOptions.merge()
                ).await()
        } catch (e: Exception) {
            Log.e("ChatRepository", "Failed to update metadata: ${e.message}")
        }
    }

    override suspend fun getChatById(chatId: String): ChatItem {
        val ref = firestore.collection(DbTable.CHATS).document(chatId)
        val doc = ref.get().await()

        if (!doc.exists()) {
            val parts = chatId.split("_")
            val userA = parts[0]
            val userB = parts[1]

            val defaultChat = mapOf(
                DbTable.PARTICIPANTS to listOf(userA, userB),
                DbTable.LAST_MESSAGE to "",
                DbTable.UPDATED_AT to Timestamp.now()
            )
            ref.set(defaultChat).await()

            return ChatItem(chatId = chatId, participants = listOf(userA, userB), lastMessage = "")
        }

        return ChatItem(
            chatId = chatId,
            participants = doc.get(DbTable.PARTICIPANTS) as? List<String> ?: emptyList(),
            lastMessage = doc.getString(DbTable.LAST_MESSAGE) ?: "",
            lastTimestamp = doc.getTimestamp(DbTable.UPDATED_AT)?.seconds
        )
    }

    override fun listenToMessages(chatId: String, onMessages: (List<Message>) -> Unit): ListenerRegistration {
        return firestore.collection(DbTable.MESSAGES)
            .document(chatId)
            .collection(DbTable.MSGS)
            .orderBy(DbTable.TIMESTAMP)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onMessages(emptyList())
                    return@addSnapshotListener
                }
                val msgs = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Message::class.java)?.copy(messageId = doc.id, chatId = chatId)
                } ?: emptyList()
                onMessages(msgs)
            }
    }

    override fun listenToUserChats(userId: String, onChats: (List<ChatItem>) -> Unit): ListenerRegistration {
        return firestore.collection(DbTable.CHATS)
            .whereArrayContains(DbTable.PARTICIPANTS, userId)
            .orderBy(DbTable.UPDATED_AT, Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onChats(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    val participants = doc.get(DbTable.PARTICIPANTS) as? List<String> ?: return@mapNotNull null
                    val other = participants.firstOrNull { it != userId } ?: return@mapNotNull null
                    ChatItem(
                        chatId = doc.id,
                        participants = participants,
                        lastMessage = doc.getString(DbTable.LAST_MESSAGE) ?: "",
                        lastTimestamp = doc.getTimestamp(DbTable.UPDATED_AT)?.seconds,
                        otherUserId = other
                    )
                } ?: emptyList()
                onChats(items)
            }
    }
}