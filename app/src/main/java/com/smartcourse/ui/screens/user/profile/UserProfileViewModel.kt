package com.smartcourse.ui.screens.user.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.Course
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.user.CourseRepository
import com.smartcourse.data.repositories.user.ProfileRepository
import com.smartcourse.data.repositories.user.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val courseRepo: CourseRepository,
    private val storageRepo: StorageRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    // --------------------------------------------------
    // STATE
    // --------------------------------------------------

    // Unified User State: The User object now contains the courses list
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _allCourses = MutableStateFlow<List<Course>>(emptyList())
    val allCourses: StateFlow<List<Course>> = _allCourses

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating

    // --------------------------------------------------
    // LOADERS
    // --------------------------------------------------

    /**
     * Loads the user and fills the transient 'courses' field using the repository
     */
    fun loadUser(userId: String) {
        viewModelScope.launch {
            val rawUser = profileRepo.loadUser(userId)
            _user.value = rawUser?.let { authRepo.populateUserDetails(it) }
        }
    }

    fun loadAllCourses() {
        viewModelScope.launch {
            _allCourses.value = courseRepo.getAllCourses()
        }
    }

    // --------------------------------------------------
    // UPDATES
    // --------------------------------------------------

    /**
     * Helper to perform DB updates and refresh the global Auth state
     */
    private fun performUpdate(userId: String, authVM: AuthViewModel, action: suspend () -> Unit) {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                action()
                // 1. Refresh this local screen
                loadUser(userId)
                // 2. Sync the global Auth state (Header, Home Screen, etc.)
                authVM.refreshUser()
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun updateName(userId: String, name: String, authVM: AuthViewModel) {
        if (name.isBlank()) return
        performUpdate(userId, authVM) { profileRepo.updateUserName(userId, name) }
    }

    fun updateBio(userId: String, bio: String, authVM: AuthViewModel) {
        performUpdate(userId, authVM) { profileRepo.updateUserBio(userId, bio) }
    }


//    fun updateRole(userId: String, role: UserRole, authVM: AuthViewModel) {
//        performUpdate(userId, authVM) { profileRepo.updateUserRole(userId, role) }
//    }

    fun addCourse(userId: String, courseId: String, authVM: AuthViewModel) {
        performUpdate(userId, authVM) { courseRepo.addUserCourse(userId, courseId) }
    }

    fun removeCourse(userId: String, courseId: String, authVM: AuthViewModel) {
        performUpdate(userId, authVM) { courseRepo.removeUserCourse(userId, courseId) }
    }

    fun updateAvatarPng(userId: String, imageBytes: ByteArray, authVM: AuthViewModel) {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                // Check session manually before calling the repository to avoid the IllegalStateException
                val baseUrl = storageRepo.uploadUserAvatar(userId, imageBytes)
                val versionedUrl = "$baseUrl?v=${System.currentTimeMillis()}"

                profileRepo.updateUserImage(userId, versionedUrl)

                // Refresh local and global state
                loadUser(userId)
                authVM.refreshUser()
            } catch (e: Exception) {
                println("Upload failed: ${e.message}")
            } finally {
                _isUpdating.value = false
            }
        }
    }
}