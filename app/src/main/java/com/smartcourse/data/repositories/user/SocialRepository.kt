package com.smartcourse.data.repositories.user

import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.models.table.TableNames
import com.smartcourse.data.models.table.UserFavoriteRow
import com.smartcourse.data.models.table.UserFavoriteTable
import com.smartcourse.data.models.table.UserFavoriteTable.USER_A
import com.smartcourse.data.models.table.UserFavoriteTable.USER_B
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.remote.firebase.FirebaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Suppress("UNCHECKED_CAST")
@Singleton
class SocialRepository @Inject constructor(
    private val client: SupabaseClient,
    private val profileRepo: ProfileRepository
) {
    suspend fun isUserFavorite(userA: String, userB: String): Boolean {
        val response = client.postgrest[TableNames.USER_FAVORITE].select {
            filter {
                eq(USER_A, userA)
                eq(USER_B, userB)
            }
            limit(1)
        }
        return response.data != "[]"
    }

    suspend fun saveUser(userA: String, userB: String) {
        client.postgrest[TableNames.USER_FAVORITE].insert(mapOf(USER_A to userA, USER_B to userB))
    }

    suspend fun unsaveUser(userA: String, userB: String) {
        client.postgrest[TableNames.USER_FAVORITE].delete {
            filter {
                eq(USER_A, userA)
                eq(USER_B, userB)
            }
        }
    }

    suspend fun getUsersInChat(chatId: String): List<User> {
        val parts = chatId.split("_")
        if (parts.size != 2) throw IllegalArgumentException("Invalid chatId")
        return listOfNotNull(profileRepo.loadUser(parts[0]), profileRepo.loadUser(parts[1]))
    }


    suspend fun getFavoriteUserIds(userId: String): Set<String> {
        val rows = client.postgrest[UserFavoriteTable.TABLE]
            .select { filter { eq(USER_A, userId) } }
            .decodeList<UserFavoriteRow>()
        return rows.map { it.userB }.toSet()
    }

    suspend fun loadRecentChats(userId: String): List<ChatItem> {
        val chatDocs = FirebaseClientProvider.firestore
            .collection("chats")
            .whereArrayContains("participants", userId)
            .get()
            .await()

        return chatDocs.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            val participants = data["participants"] as? List<String> ?: emptyList()
            val otherId = participants.firstOrNull { it != userId } ?: return@mapNotNull null

            ChatItem(
                chatId = doc.id,
                participants = participants,
                lastMessage = data["last_message"] as? String ?: "",
                lastTimestamp = data["updated_at"] as? Long ?: 0L,
                otherUserId = otherId,
                otherUser = profileRepo.loadUser(otherId) // Use profileRepo here
            )
        }.sortedByDescending { it.lastTimestamp }
    }
}