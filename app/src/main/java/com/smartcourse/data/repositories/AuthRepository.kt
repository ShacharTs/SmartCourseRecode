package com.smartcourse.data.repositories

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.smartcourse.auth.AuthResult
import com.smartcourse.auth.AuthStrategy
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.user.CourseRepository
import com.smartcourse.data.repositories.user.NotificationRepository
import com.smartcourse.data.repositories.user.ProfileRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val profileRepo: ProfileRepository,
    private val notificationRepo: NotificationRepository,
    private val courseRepo: CourseRepository,

) {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // ------------------------------------------------------------
    // LOGIN ENTRY (strategy handles provider)
    // ------------------------------------------------------------
    suspend fun loginWith(
        strategy: AuthStrategy,
        context: Context
    ): AuthResult {

        val result = strategy.login(context)

        if (result.success && result.userId != null) {
            loadOrCreateUser(result.userId)
            restoreValidSession()
        }

        return result
    }


    // ------------------------------------------------------------
    // USER PROFILE
    // ------------------------------------------------------------
    suspend fun loadOrCreateUser(userId: String): User {
        var profile = profileRepo.loadUser(userId)

        if (profile == null) {
            val u = supabase.auth.currentUserOrNull()

            profileRepo.createUser(
                id = userId,
                email = u?.email ?: "",
                name = u?.userMetadata?.get("full_name")?.toString() ?: "",
                image = u?.userMetadata?.get("avatar_url")?.toString() ?: "",
                bio = u?.userMetadata?.get("user_bio")?.toString() ?: "",
                role = UserRole.TEMP.name
            )

            profile = profileRepo.loadUser(userId)
                ?: error("Failed to create user profile")
        }

        _currentUser.value = profile
        return profile
    }

    // ------------------------------------------------------------
    // SESSION RESTORE
    // ------------------------------------------------------------
    suspend fun restoreValidSession(): User? {
        val session = supabase.auth.currentSessionOrNull() ?: return null
        val userId = session.user?.id ?: return null

        val user = loadOrCreateUser(userId)


        notificationRepo.ensureFcmTokenSaved()

        return user
    }


    // ------------------------------------------------------------
    // LOGOUT
    // ------------------------------------------------------------
    suspend fun logout(strategy: AuthStrategy? = null) {
        // 1. Remove token from backend
        notificationRepo.removeFcmToken()

        // 2. Invalidate local FCM token
        FirebaseMessaging.getInstance().deleteToken()

        // 3. Logout auth providers
        strategy?.logout()
        supabase.auth.signOut()
        supabase.auth.clearSession()

        // 4. Clear app state
        _currentUser.value = null
    }




    suspend fun registerWithEmail(
        email: String,
        password: String
    ) {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        // After login, load or create user profile
        val sessionUser = supabase.auth.currentSessionOrNull()?.user
            ?: error("Registration succeeded but no session user")

        loadOrCreateUser(sessionUser.id)
    }


    suspend fun populateUserDetails(user: User): User {
        return try {
            // 1. Fetch the relationship links (User <-> Course)
            val userCourseLinks = courseRepo.getUserCourses(user.userId)

            // 2. Map those links to full Course objects
            val fullCourses = userCourseLinks.mapNotNull { link ->
                courseRepo.getCourseById(link.course_id)
            }

            // 3. Return a copy of the user with the courses list filled
            user.copy(courses = fullCourses)
        } catch (e: Exception) {
            Log.e("CourseRepo", "Error populating details for ${user.userId}", e)
            user // Return basic user if the enrichment fails
        }
    }


}
