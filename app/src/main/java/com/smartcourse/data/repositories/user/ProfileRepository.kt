package com.smartcourse.data.repositories.user

import com.smartcourse.data.models.table.TableNames
import com.smartcourse.data.models.table.UserTable
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.models.usermodel.serialName
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import javax.inject.Inject
import javax.inject.Singleton




@Singleton
class ProfileRepository @Inject constructor(
    private val client: SupabaseClient
) {
    suspend fun createUser(id: String, email: String, name: String, image: String, role: String, bio: String) {
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

    suspend fun loadUser(id: String): User? {
        return client.postgrest[TableNames.USERTABLE]
            .select { filter { eq(UserTable.ID, id) } }
            .decodeList<User>()
            .firstOrNull()
    }



    suspend fun updateUserName(userId: String, name: String) {
        client.from(TableNames.USERTABLE).update(mapOf(UserTable.NAME to name)) {
            filter { eq(UserTable.ID, userId) }
        }
    }

    suspend fun updateUserBio(userId: String, bio: String) {
        client.from(TableNames.USERTABLE).update(mapOf(UserTable.BIO to bio)) {
            filter { eq(UserTable.ID, userId) }
        }
    }

    suspend fun searchUsers(query: String): List<User> {
        val q = query.trim()
        if (q.isBlank()) return emptyList()
        return client.postgrest[TableNames.USERTABLE]
            .select { filter { ilike(UserTable.NAME, "$q%") } }
            .decodeList<User>()
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



    suspend fun syncGoogleAvatar() {
        val u = client.auth.currentUserOrNull() ?: return
        val metadata = u.userMetadata ?: return
        val avatar = (metadata["avatar_url"] as? JsonPrimitive)?.contentOrNull
            ?: (metadata["picture"] as? JsonPrimitive)?.contentOrNull ?: ""

        if (avatar.isNotBlank()) {
            updateUserImage(id = u.id, image = avatar)
        }
    }

    // Keep internal helper or move to separate Image/Storage logic
    suspend fun updateUserImage(id: String, image: String?) {
        if (image.isNullOrBlank()) return
        client.from(TableNames.USERTABLE).update(mapOf(UserTable.IMAGE to image)) {
            filter { eq(UserTable.ID, id) }
        }
    }
}