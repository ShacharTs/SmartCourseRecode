package com.smartcourse.ui.screens.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.remote.firebase.FirebaseUserProvider
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.ChatRepository
import com.smartcourse.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    authRepo: AuthRepository,
    private val repo: ChatRepository,
    private val userRepo: UserRepository
) : ViewModel() {

    // Read ONCE — chat list does not react to auth changes
    private val myId: String =
        requireNotNull(authRepo.currentUser.value?.userId) {
            "ChatListViewModel created without logged-in user"
        }

    private val _chats = MutableStateFlow<List<ChatItem>>(emptyList())
    val chats: StateFlow<List<ChatItem>> = _chats.asStateFlow()

    private var listener: ListenerRegistration? = null

    init {
        start()
    }

    /**
     * Single entry point.
     * Ensures Firebase + starts listener.
     */
    private fun start() {
        viewModelScope.launch {
            ensureFirebaseReady()
            startListening()
        }
    }
    fun refresh() {
        startListening()
    }


    /**
     * Firebase readiness is INTERNAL.
     * UI must never call this.
     */
    private suspend fun ensureFirebaseReady() {
        FirebaseUserProvider.ensureFirebaseUser()
    }

    /**
     * Listen to chats for current user.
     */
    private fun startListening() {
        listener?.remove()

        listener = repo.listenToUserChats(myId) { list ->
            viewModelScope.launch {
                val enriched = list.map { chatItem ->
                    val otherUser = runCatching {
                        userRepo.loadUser(chatItem.otherUserId)
                    }.getOrNull()

                    chatItem.copy(otherUser = otherUser)
                }

                _chats.value = enriched
            }
        }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}