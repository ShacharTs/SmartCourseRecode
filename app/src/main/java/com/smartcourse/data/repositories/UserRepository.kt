package com.smartcourse.data.repositories

import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.models.usermodel.Course
import com.smartcourse.data.models.usermodel.TableNames
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserCourseTable
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.models.usermodel.UserTable
import com.smartcourse.data.models.usermodel.serialName
import com.smartcourse.data.remote.firebase.FirebaseClientProvider.firestore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val client: SupabaseClient
) {

    /**
     * create user in user_table
     */
    suspend fun createUser(id: String, email: String, name: String, image: String, role: String) {
        client.postgrest[TableNames.USERTABLE].insert(
            mapOf(
                UserTable.ID to id,
                UserTable.NAME to name,
                UserTable.EMAIL to email,
                UserTable.IMAGE to image,
                UserTable.ROLE to role
            )
        )
    }

    /**
     * load user from user_table
     */
    suspend fun loadUser(id: String): User? {
        return client.postgrest[TableNames.USERTABLE]
            .select { filter { eq(UserTable.ID, id) } }
            .decodeList<User>()
            .firstOrNull()
    }

    suspend fun updateUserImage(id: String, image: String?) {
        if (image.isNullOrBlank()) return

        client
            .from(TableNames.USERTABLE)
            .update(
                mapOf(UserTable.IMAGE to image)
            ) {
                filter {
                    eq(UserTable.ID, id)
                }
            }
    }



    suspend fun updateUserRole(id: String, role: UserRole) {
        client
            .from(TableNames.USERTABLE)
            .update(
                mapOf(UserTable.ROLE to role.serialName())
            ){
                filter {
                    eq(UserTable.ID, id)
                }
            }
    }





    /**
     * Get all users except the user with the given ID
     */
    suspend fun getAllUsersExcept(myId: String): List<User> {
        return client.postgrest[TableNames.USERTABLE]
            .select {
                filter {
                    neq(UserTable.ID, myId)
                }
            }
            .decodeList<User>()
    }


    suspend fun getUserCourses(userId: String): List<UserCourseTable> {
        return client.postgrest[TableNames.USER_COURSES]
            .select {
                filter {
                    eq(UserTable.ID, userId)
                }
            }
            .decodeList<UserCourseTable>()
    }

    suspend fun getCourseById(courseId: String): Course? {
        return client.postgrest[TableNames.COURSE_LIST]
            .select {
                filter { eq("id", courseId) }
                limit(1)
            }
            .decodeSingleOrNull<Course>()
    }






    suspend fun matchUserByRole(
        myId: String,
        targetRole: UserRole
    ): List<User> {

        // 1. Load all users except me
        val allOthers = getAllUsersExcept(myId)

        // 2. Filter by target role
        val usersWithRole = allOthers.filter {
            it.role == targetRole
        }

        // 3. Load my courses
        val myCourses = getUserCourses(myId)
            .map { it.course_id }
            .toSet()

        val matching = mutableListOf<User>()

        // 4. Compare courses
        for (other in usersWithRole) {

            val theirCourses = getUserCourses(other.getUID())
                .map { it.course_id }
                .toSet()

            if (myCourses.intersect(theirCourses).isNotEmpty()) {
                matching.add(other)
            }
        }

        return matching
    }



    suspend fun syncGoogleAvatar() {
        val u = client.auth.currentUserOrNull() ?: return
        val metadata = u.userMetadata ?: return

        val avatar =
            (metadata["avatar_url"] as? JsonPrimitive)?.contentOrNull
                ?: (metadata["picture"] as? JsonPrimitive)?.contentOrNull
                ?: ""

        if (avatar.isNotBlank()) {
            updateUserImage(id = u.id, image = avatar)
        }
    }


    /**
     * Given a chatId of the form "userA_userB", return both users.
     */
    suspend fun getUsersInChat(chatId: String): List<User> {
        val parts = chatId.split("_")

        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid chatId format: $chatId")
        }

        val uid1 = parts[0]
        val uid2 = parts[1]

        val user1 = loadUser(uid1)
        val user2 = loadUser(uid2)

        return listOfNotNull(user1, user2)
    }


    suspend fun loadRecentChats(userId: String): List<ChatItem> {
        val chatDocs = firestore
            .collection("chats")
            .whereArrayContains("participants", userId)
            .get()
            .await()

        if (chatDocs.isEmpty) return emptyList()

        val result = mutableListOf<ChatItem>()

        for (doc in chatDocs.documents) {
            val data = doc.data ?: continue

            val chatId = doc.id
            val participants = data["participants"] as? List<String> ?: emptyList()
            val lastMessage = data["last_message"] as? String ?: ""
            val timestamp = data["updated_at"] as? Long ?: 0L

            // Identify the other person
            val otherId = participants.firstOrNull { it != userId } ?: continue

            // Load full user object from Supabase
            val otherUser = loadUser(otherId)

            result.add(
                ChatItem(
                    chatId = chatId,
                    participants = participants,
                    lastMessage = lastMessage,
                    lastTimestamp = timestamp,
                    otherUserId = otherId,
                    otherUser = otherUser
                )
            )
        }

        // Sort newest first
        return result.sortedByDescending { it.lastTimestamp }
    }

}
