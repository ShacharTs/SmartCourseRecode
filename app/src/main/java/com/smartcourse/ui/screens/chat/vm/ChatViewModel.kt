package com.smartcourse.ui.screens.chat.vm

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.ktx.storage
import com.smartcourse.data.models.chat.Message
import com.smartcourse.data.remote.firebase.FirebaseUserProvider
// Change from ChatRepository to IChatRepository
import com.smartcourse.data.repositories.chat.IChatRepository
import com.smartcourse.data.repositories.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepo: IChatRepository,
    private val userRepo: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val chatId: String =
        checkNotNull(savedStateHandle["chatId"]) {
            "chatId is required"
        }

    private var listener: ListenerRegistration? = null

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    /**
     * Start listening to incoming messages for this chat.
     */

    fun startListening(chatId: String) {
        viewModelScope.launch {
            FirebaseUserProvider.ensureFirebaseUser()
            listener?.remove()

            listener = chatRepo.listenToMessages(chatId) { msgs ->
                // Sort by descending timestamp so newest = index 0
                _messages.value = msgs.sortedByDescending { it.timestamp }
            }
        }
    }

    /**
     * Stop listening when leaving chat screen.
     */
    fun stopListening() {
        listener?.remove()
        listener = null
    }

    /**
     * Sends a new message.
     */
    fun sendMessage(
        chatId: String,
        text: String,
        myId: String,
        otherId: String,
        type: String = "text" // Add a default type
    ) {
        if (text.isBlank() && type == "text") return

        viewModelScope.launch {
            val currentUser = userRepo.loadUser(myId)
            val name = currentUser?.displayName ?: "Unknown User"

            val msg = Message(
                chatId = chatId,
                text = text,
                senderId = myId,
                senderName = name,
                receiverId = otherId,
                timestamp = Timestamp.now(),
                type = type // Pass the type here
            )

            chatRepo.sendMessage(chatId, msg, myId, otherId, name)
        }
    }

    suspend fun getReceiverId(chatId: String, mySupabaseId: String): String {
        val chat = chatRepo.getChatById(chatId)

        val receiver = chat.participants.firstOrNull { it != mySupabaseId }
            ?: throw IllegalStateException("Chat has no other participant.")

        return receiver
    }
    // todo useable ?
//    fun openChatWith(otherUserId: String, myId: String, navController: NavController) {
//        viewModelScope.launch {
//            val chatId = chatRepo.ensureChatExists(myId, otherUserId)
//            navController.navigate("chat/$chatId")
//        }
//    }

    fun ensureFirebaseReady() {
        viewModelScope.launch {
            FirebaseUserProvider.ensureFirebaseUser()
        }
    }

//    suspend fun getChatParticipants(chatId: String): Pair<String, String> {
//        val chat = chatRepo.getChatById(chatId)
//        if (chat.participants.size != 2) {
//            throw IllegalStateException("Chat must have exactly 2 participants.")
//        }
//        return Pair(chat.participants[0], chat.participants[1])
//    }

    suspend fun getBothUsers(chatId: String) = userRepo.getUsersInChat(chatId)

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }


    // Import the storage reference
    private val storage = com.google.firebase.ktx.Firebase.storage.reference

    private suspend fun uploadImage(uri: Uri): String {
        // Create a unique file path for each image
        val fileName = "chat_images/${java.util.UUID.randomUUID()}.jpg"
        val fileRef = storage.child(fileName)

        return try {
            // Use kotlinx-coroutines-play-services to await the upload task
            fileRef.putFile(uri).await()
            // Get the public download URL once the upload is complete
            fileRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            android.util.Log.e("ChatViewModel", "Upload failed: ${e.message}")
            "" // Return empty if upload fails
        }
    }

    fun sendImageMessage(chatId: String, uri: Uri, myId: String, otherId: String) {
        viewModelScope.launch {
            val imageUrl = uploadImage(uri)
            if (imageUrl.isNotEmpty()) {
                // Reusing your existing sendMessage logic with type = "image"
                sendMessage(chatId, imageUrl, myId, otherId, type = "image")
            }
        }
    }

    fun sendLocationMessage(chatId: String, lat: Double?, lng: Double?, myId: String, otherId: String) {
        val locationData = "$lat,$lng"
        sendMessage(chatId, locationData, myId, otherId, type = "location")
    }
}