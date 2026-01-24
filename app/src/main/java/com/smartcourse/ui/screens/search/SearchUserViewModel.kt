package com.smartcourse.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchUserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUserUiState())
    val uiState: StateFlow<SearchUserUiState> = _uiState

    private var myUserId: String? = null
    private var myRole: UserRole? = null
    private var myCourses: Set<String> = emptySet()

    init {
        viewModelScope.launch {
            val me = authRepo.restoreValidSession() ?: return@launch

            myUserId = me.userId
            myRole = me.role

            myCourses = userRepository
                .getUserCourses(me.userId)
                .mapNotNull { link ->
                    userRepository.getCourseById(link.course_id)
                }
                .map { it.name }
                .toSet()
        }
    }


    // 1. Add this variable to your class to track the current search
    private var searchJob: Job? = null

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(
            query = query,
            isLoading = true
        )

        // 2. Cancel the previous search immediately to prevent race conditions
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            // 3. Wait 300ms. If the user types again, this job is cancelled
            // and the database is never called, saving resources.
            delay(300)

            if (query.isBlank()) {
                _uiState.value = SearchUserUiState()
                return@launch
            }

            val users = userRepository.searchUsers(query)

            val filteredUsers = users
                .filter { it.userId != myUserId }
                // 4. Safety Filter: Double-check that the name actually
                // starts with the query, matching your repository logic.
                .filter { it.displayName.startsWith(query, ignoreCase = true) }
                .filter { user ->
                    when (myRole) {
                        UserRole.STUDENT -> user.role == UserRole.TUTOR
                        UserRole.TUTOR -> user.role == UserRole.STUDENT
                        else -> false
                    }
                }

            val rows = filteredUsers.map { user ->
                val links = userRepository.getUserCourses(user.userId)
                val courses = links
                    .mapNotNull { link ->
                        userRepository.getCourseById(link.course_id)
                    }
                    .map { it.name }

                SearchUserRowState(user = user, courses = courses)
            }

            _uiState.value = _uiState.value.copy(
                results = rows,
                isLoading = false
            )
        }
    }
}
