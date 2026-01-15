@file:Suppress("UNCHECKED_CAST")

package com.smartcourse.data.repositories

import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.models.table.TableNames
import com.smartcourse.data.models.table.UserCourseTable
import com.smartcourse.data.models.table.UserFavoriteRow
import com.smartcourse.data.models.table.UserFavoriteTable
import com.smartcourse.data.models.table.UserFavoriteTable.USER_A
import com.smartcourse.data.models.table.UserFavoriteTable.USER_B
import com.smartcourse.data.models.table.UserTable
import com.smartcourse.data.models.usermodel.Course
import com.smartcourse.data.models.usermodel.DomainUser
import com.smartcourse.data.models.usermodel.Student
import com.smartcourse.data.models.usermodel.Tutor
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.models.usermodel.serialName
import com.smartcourse.data.remote.firebase.FirebaseClientProvider.firestore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("USELESS_IS_CHECK")
@Singleton
class UserRepository @Inject constructor(
    private val client: SupabaseClient
) {

    /**
     * create user in user_table
     */
    suspend fun createUser(
        id: String,
        email: String,
        name: String,
        image: String,
        role: String,
        bio: String
    ) {
        client.postgrest[TableNames.USERTABLE].insert(
            mapOf(
                UserTable.ID to id,
                UserTable.NAME to name,
                UserTable.EMAIL to email,
                UserTable.IMAGE to image,
                UserTable.ROLE to role,
                UserTable.BIO to bio,
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
            ) {
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

    suspend fun getFavoriteUserIds(userId: String): Set<String> {
        val rows = client
            .postgrest[UserFavoriteTable.TABLE]
            .select {
                filter {
                    eq(USER_A, userId)
                }
            }
            .decodeList<UserFavoriteRow>()

        return rows.map { it.userB }.toSet()
    }

    suspend fun isUserFavorite(userA: String, userB: String): Boolean {
        return try {
            val response = client
                .postgrest[TableNames.USER_FAVORITE]
                .select {
                    filter {
                        eq(USER_A, userA)
                        eq(USER_B, userB)
                    }
                    limit(1)
                }

            val raw = response.data
            val exists = raw != "[]"

            android.util.Log.d(
                "FAVORITE_CHECK",
                "exists=$exists raw=$raw"
            )

            exists
        } catch (e: Exception) {
            android.util.Log.e("FAVORITE_CHECK", "isUserFavorite FAILED", e)
            false
        }
    }


    suspend fun saveUser(userA: String, userB: String) {
        try {
            client.postgrest[TableNames.USER_FAVORITE].insert(
                mapOf(
                    USER_A to userA,
                    USER_B to userB
                )
            )
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun unsaveUser(userA: String, userB: String) {
        try {
            client.postgrest[TableNames.USER_FAVORITE].delete {
                filter {
                    eq(USER_A, userA)
                    eq(USER_B, userB)
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }


    suspend fun updateUserName(userId: String, name: String) {
        try {
            client
                .from(TableNames.USERTABLE)
                .update(mapOf(UserTable.NAME to name)) {
                    filter { eq(UserTable.ID, userId) }
                }
        } catch (e: Exception) {
            throw e
        }
    }


    suspend fun updateUserBio(userId: String, bio: String) {
        try {
            client
                .from(TableNames.USERTABLE)
                .update(mapOf(UserTable.BIO to bio)) {
                    filter { eq(UserTable.ID, userId) }
                }
        } catch (e: Exception) {
            throw e
        }
    }


    suspend fun removeUserCourse(userId: String, courseId: String) {
        try {
            client
                .from(TableNames.USER_COURSES)
                .delete {
                    filter {
                        eq(UserTable.ID, userId)
                        eq("course_id", courseId)
                    }
                }
        } catch (e: Exception) {
            throw e
        }
    }



    suspend fun getAllCourses(): List<Course> {
        return try {
            client
                .from(TableNames.COURSE_LIST)
                .select()
                .decodeList()
        } catch (e: Exception) {
            throw e
        }
    }



    suspend fun addUserCourse(userId: String, courseId: String) {
        client
            .from(TableNames.USER_COURSES)
            .upsert(
                value = mapOf(
                    "user_id" to userId,
                    "course_id" to courseId
                ),
                onConflict = "user_id,course_id"
            )
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

    // todo remove later when app done
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


    // later use
    suspend fun toDomainUser(user: User): DomainUser? =
        when (user.role) {

            UserRole.STUDENT -> {
                val links = getUserCourses(user.userId)
                val courses = links.mapNotNull { getCourseById(it.course_id) }
                Student(user, courses)
            }

            UserRole.TUTOR -> {
                val links = getUserCourses(user.userId)
                val courses = links.mapNotNull { getCourseById(it.course_id) }
                Tutor(user, courses)
            }

            UserRole.TEMP -> null

            else -> null
        }


    suspend fun searchUsers(query: String): List<User> {
        val q = query.trim()

        if (q.isBlank()) return emptyList()

        return client.postgrest[TableNames.USERTABLE]
            .select {
                filter {
                    ilike(UserTable.NAME, "$q%")
                }
            }
            .decodeList<User>()
    }

    suspend fun countUserFavorites(userId: String): Int {
        return client.postgrest[UserFavoriteTable.TABLE]
            .select {
                filter {
                    eq(USER_B, userId)
                }
            }
            .decodeList<UserFavoriteRow>()
            .size
    }


    suspend fun uploadUserAvatar(
        userId: String,
        imageBytes: ByteArray
    ): String {
        val bucketName = "user_profile_image"
        val objectPath = "users/$userId.png"

        val bucket = client.storage.from(bucketName)

        bucket.upload(
            path = objectPath,
            data = imageBytes,
            upsert = true
        )

        val rawBase = client.supabaseUrl.trimEnd('/')
        val baseUrl =
            if (rawBase.startsWith("http://") || rawBase.startsWith("https://")) {
                rawBase
            } else {
                "https://$rawBase"
            }

        return "$baseUrl/storage/v1/object/public/$bucketName/$objectPath"
    }






}
