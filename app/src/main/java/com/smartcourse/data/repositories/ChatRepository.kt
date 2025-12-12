@file:Suppress("UNCHECKED_CAST")

package com.smartcourse.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.models.chat.Message
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChatRepository @Inject constructor (
    private val firestore: FirebaseFirestore
){

    //private val firestore: FirebaseFirestore = FirebaseClientProvider.firestore

    /**
     * Deterministic chat ID for any two users.
     * Sorting ensures chatId(userA, userB) == chatId(userB, userA)
     */
    fun createChatId(userId1: String, userId2: String): String {
        return listOf(userId1, userId2).sorted().joinToString("_")
    }

    // ------------------------------------------------------------
    //  CHAT CREATION + SAFETY
    // ------------------------------------------------------------

    /**
     * Ensures a chat exists BEFORE entering the screen.
     * If it doesn't exist → creates it with correct participants.
     */
    suspend fun ensureChatExists(userA: String, userB: String): String {
        val chatId = createChatId(userA, userB)
        val ref = firestore.collection(DbTable.CHATS).document(chatId)

        val snap = ref.get().await()
        if (!snap.exists()) {
            ref.set(
                mapOf(
                    DbTable.PARTICIPANTS to listOf(userA, userB),
                    DbTable.LAST_MESSAGE to "",
                    DbTable.UPDATED_AT to com.google.firebase.Timestamp.now()
                )
            ).await()
        }

        return chatId
    }

    // ------------------------------------------------------------
    //  SEND MESSAGE
    // ------------------------------------------------------------

    suspend fun sendMessage(
        chatId: String,
        message: Message,
        myId: String,
        otherId: String
    ) {
        // Write message inside chat/{chatId}/msgs
        firestore.collection(DbTable.MESSAGES)
            .document(chatId)
            .collection(DbTable.MSGS)
            .add(message)
            .await()

        // Update metadata only — never touch participants here
        updateChatMetadata(chatId, message.text)
    }

    /**
     * Update last message + timestamp only.
     */
    private fun updateChatMetadata(chatId: String, lastMessage: String) {
        firestore.collection(DbTable.CHATS)
            .document(chatId)
            .set(
                mapOf(
                    DbTable.LAST_MESSAGE to lastMessage,
                    DbTable.UPDATED_AT to com.google.firebase.Timestamp.now()
                ),
                SetOptions.merge()
            )
    }

    // ------------------------------------------------------------
    //  GET SINGLE CHAT
    // ------------------------------------------------------------

    suspend fun getChatById(chatId: String): ChatItem {
        val ref = firestore.collection(DbTable.CHATS).document(chatId)
        val doc = ref.get().await()

        if (!doc.exists()) {
            // Extract the two user IDs from the chatId
            val parts = chatId.split("_")
            if (parts.size != 2) {
                throw IllegalStateException("Invalid chatId format: $chatId")
            }

            val userA = parts[0]
            val userB = parts[1]

            // Create default chat document
            val defaultChat = mapOf(
                DbTable.PARTICIPANTS to listOf(userA, userB),
                DbTable.LAST_MESSAGE to "",
                DbTable.UPDATED_AT to com.google.firebase.Timestamp.now()
            )

            ref.set(defaultChat).await()

            return ChatItem(
                chatId = chatId,
                participants = listOf(userA, userB),
                lastMessage = "",
                lastTimestamp = null
            )
        }

        val participants = doc.get(DbTable.PARTICIPANTS) as? List<String> ?: emptyList()

        return ChatItem(
            chatId = chatId,
            participants = participants,
            lastMessage = doc.getString(DbTable.LAST_MESSAGE) ?: "",
            lastTimestamp = doc.getTimestamp(DbTable.UPDATED_AT)?.seconds
        )
    }


    // ------------------------------------------------------------
    //  LIVE MESSAGE LISTENER
    // ------------------------------------------------------------

    fun listenToMessages(
        chatId: String,
        onMessages: (List<Message>) -> Unit
    ): ListenerRegistration {

        return firestore.collection(DbTable.MESSAGES)
            .document(chatId)
            .collection(DbTable.MSGS)
            .orderBy(DbTable.TIMESTAMP)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onMessages(emptyList())
                    return@addSnapshotListener
                }

                val msgs = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Message::class.java)?.copy(
                        messageId = doc.id,
                        chatId = chatId
                    )
                }

                onMessages(msgs)
            }
    }

    // ------------------------------------------------------------
    //  LIST CHATS FOR USER
    // ------------------------------------------------------------

    fun listenToUserChats(
        userId: String,
        onChats: (List<ChatItem>) -> Unit
    ): ListenerRegistration {

        return firestore.collection(DbTable.CHATS)
            .whereArrayContains(DbTable.PARTICIPANTS, userId)
            .addSnapshotListener { snapshot, error ->

                if (error != null || snapshot == null) {
                    onChats(emptyList())
                    return@addSnapshotListener
                }

                val items = snapshot.documents.mapNotNull { doc ->

                    val participants =
                        doc.get(DbTable.PARTICIPANTS) as? List<String> ?: return@mapNotNull null

                    val other = participants.firstOrNull { it != userId }
                        ?: return@mapNotNull null // Safety

                    ChatItem(
                        chatId = doc.id,
                        participants = participants,
                        lastMessage = doc.getString(DbTable.LAST_MESSAGE) ?: "",
                        lastTimestamp = doc.getTimestamp(DbTable.UPDATED_AT)?.seconds,
                        otherUserId = other
                    )
                }

                onChats(items)
            }
    }







}
