package com.smartcourse.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// UI-only row state (NOT domain, NOT repository)
data class SearchUserRowState(
    val user: User,
    val courses: List<String>
)

data class SearchUserUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<SearchUserRowState> = emptyList()
)

@HiltViewModel
class SearchUserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val supabaseClient: SupabaseClient
) : ViewModel() {


    private var myCourses: Set<String> = emptySet()

    private val _uiState = MutableStateFlow(SearchUserUiState())
    val uiState: StateFlow<SearchUserUiState> = _uiState

    private var myRole: UserRole? = null
    private var myUserId: String? = null

    init {
        viewModelScope.launch {
            val authUser = supabaseClient.auth.currentUserOrNull()
                ?: return@launch

            myUserId = authUser.id
            myRole = userRepository.loadUser(authUser.id)?.role


            val myLinks = userRepository.getUserCourses(authUser.id)
            myCourses = myLinks
                .mapNotNull { link ->
                    userRepository.getCourseById(link.course_id)
                }
                .map { it.name }
                .toSet()
        }
    }


    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(
            query = query,
            isLoading = true
        )

        viewModelScope.launch {
            if (query.isBlank()) {
                _uiState.value = SearchUserUiState()
                return@launch
            }

            val users = userRepository.searchUsers(query)

            val filteredUsers = users
                .filter { it.userId != myUserId } // 🚫 never show myself
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

                SearchUserRowState(
                    user = user,
                    courses = courses
                )
            }

            _uiState.value = _uiState.value.copy(
                results = rows,
                isLoading = false
            )
        }
    }
}
