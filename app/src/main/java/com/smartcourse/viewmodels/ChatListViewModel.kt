package com.smartcourse.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.remote.firebase.FirebaseUserProvider
import com.smartcourse.data.repositories.ChatRepository
import com.smartcourse.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val repo: ChatRepository,
    private val userRepo: UserRepository
) : ViewModel() {

    private val _chats = MutableStateFlow<List<ChatItem>>(emptyList())
    val chats = _chats
    private var listener: ListenerRegistration? = null



    /**
     * Load the chats for the current user.
     */
    fun loadUserChats(myId: String) {
        viewModelScope.launch {
            FirebaseUserProvider.ensureFirebaseUser()
            listener?.remove()

            listener = repo.listenToUserChats(myId) { list ->
                viewModelScope.launch {
                    val enriched = list.map { chatItem ->

                        val user = try {
                            userRepo.loadUser(chatItem.otherUserId)
                        } catch (e: Exception) {
                            null
                        }

                        chatItem.copy(otherUser = user)
                    }

                    _chats.value = enriched
                }
            }
        }
    }

    /**
     * Check if the Firebase user is ready. If not, wait for it to be ready.
     */
    fun ensureFirebaseReady() {
        viewModelScope.launch {
            FirebaseUserProvider.ensureFirebaseUser()
        }
    }

    /**
     * Called when the ViewModel is no longer used and will be destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }





}

