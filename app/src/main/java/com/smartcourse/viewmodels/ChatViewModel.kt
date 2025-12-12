package com.smartcourse.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.firestore.ListenerRegistration
import com.smartcourse.data.models.chat.Message
import com.smartcourse.data.repositories.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp
import com.smartcourse.data.remote.firebase.FirebaseUserProvider
import com.smartcourse.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ChatRepository,
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
        listener?.remove()

        listener = repo.listenToMessages(chatId) { msgs ->
            _messages.value = msgs
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

        val msg = Message(
            chatId = chatId,
            text = text,
            senderId = myId,
            timestamp = Timestamp.now(),
            type = "text",
        )

        viewModelScope.launch {
            repo.sendMessage(
                chatId = chatId,
                message = msg,
                myId = myId,
                otherId = otherId
            )
        }
    }


    suspend fun getReceiverId(chatId: String, mySupabaseId: String): String {
        val chat = repo.getChatById(chatId)

        val receiver = chat.participants.firstOrNull { it != mySupabaseId }
            ?: throw IllegalStateException("Chat has no other participant.")

        return receiver
    }


    fun openChatWith(otherUserId: String, myId: String, navController: NavController) {
        viewModelScope.launch {
            val chatId = repo.ensureChatExists(myId, otherUserId)
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
        val chat = repo.getChatById(chatId)
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
