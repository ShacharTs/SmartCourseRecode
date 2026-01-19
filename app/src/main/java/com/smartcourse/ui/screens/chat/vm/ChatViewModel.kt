package com.smartcourse.ui.screens.chat.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import com.smartcourse.data.models.chat.Message
import com.smartcourse.data.remote.firebase.FirebaseUserProvider
import com.smartcourse.data.repositories.ChatRepository
import com.smartcourse.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepo: ChatRepository,
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
            // BLOCK until Firebase is authenticated
            FirebaseUserProvider.ensureFirebaseUser()

            listener?.remove()

            listener = chatRepo.listenToMessages(chatId) { msgs ->
                _messages.value = msgs
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
        otherId: String
    ) {
        if (text.isBlank()) return

        viewModelScope.launch {
            // Fetch current user details to get the sender's name
            val currentUser = userRepo.loadUser(myId)
            val name = currentUser?.displayName ?: "Unknown User"

            val msg = Message(
                chatId = chatId,
                text = text,
                senderId = myId,
                senderName = name,
                receiverId = otherId,
                timestamp = Timestamp.now(),
                type = "text",
            )

            chatRepo.sendMessage(
                chatId = chatId,
                message = msg,
                myId = myId,
                otherId = otherId,
                senderName = name
            )
        }
    }

    suspend fun getReceiverId(chatId: String, mySupabaseId: String): String {
        val chat = chatRepo.getChatById(chatId)

        val receiver = chat.participants.firstOrNull { it != mySupabaseId }
            ?: throw IllegalStateException("Chat has no other participant.")

        return receiver
    }


    fun openChatWith(otherUserId: String, myId: String, navController: NavController) {
        viewModelScope.launch {
            val chatId = chatRepo.ensureChatExists(myId, otherUserId)
            navController.navigate("chat/$chatId")
        }
    }


    /**
     * Ensures Firebase is authenticated before any chat actions.
     */
    fun ensureFirebaseReady() {
        viewModelScope.launch {
            FirebaseUserProvider.ensureFirebaseUser()
        }
    }


    /**
     * Get both users participating in this chat.
     */
    suspend fun getChatParticipants(chatId: String): Pair<String, String> {
        val chat = chatRepo.getChatById(chatId)
        if (chat.participants.size != 2) {
            throw IllegalStateException("Chat must have exactly 2 participants.")
        }
        return Pair(chat.participants[0], chat.participants[1])
    }


    /**
     * Load both User objects participating in this chat.
     */
    suspend fun getBothUsers(chatId: String) = userRepo.getUsersInChat(chatId)


    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}