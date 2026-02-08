package com.smartcourse.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.data.repositories.AuthRepository
import com.smartcourse.data.repositories.user.CourseRepository
import com.smartcourse.data.repositories.user.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchUserViewModel @Inject constructor(
    private val courseRepo: CourseRepository,
    private val profileRepo: ProfileRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUserUiState())
    val uiState: StateFlow<SearchUserUiState> = _uiState

    private var myUserId: String? = null
    private var myRole: UserRole? = null
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            val me = authRepo.restoreValidSession() ?: return@launch
            myUserId = me.userId
            myRole = me.role


            val allCourses = courseRepo.getAllCourses().map { it.name }
            _uiState.value = _uiState.value.copy(availableCourses = allCourses)


            performSearch()
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        performSearch()
    }

    fun onCourseFilterChanged(courseName: String?) {
        _uiState.value = _uiState.value.copy(selectedCourse = courseName)
        performSearch()
    }

    private fun performSearch() {
        val currentQuery = _uiState.value.query
        val currentFilter = _uiState.value.selectedCourse

        _uiState.value = _uiState.value.copy(isLoading = true)
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(300)


            if (currentQuery.isBlank() && currentFilter == null && _uiState.value.results.isEmpty()) {

            }

            // 1. Fetch users (Now returns everyone if currentQuery is "")
            val users = profileRepo.searchUsers(currentQuery)

            val rows = users
                .filter { it.userId != myUserId }
                .filter { user ->
                    when (myRole) {
                        UserRole.STUDENT -> user.role == UserRole.TUTOR
                        UserRole.TUTOR -> user.role == UserRole.STUDENT
                        else -> false
                    }
                }
                .map { user ->
                    val links = courseRepo.getUserCourses(user.userId)
                    val courses = links
                        .mapNotNull { courseRepo.getCourseById(it.course_id) }
                        .map { it.name }
                    SearchUserRowState(user = user, courses = courses)
                }
                .filter { row ->
                    // 2. This is why "All" works now:
                    // If currentFilter is null (The "All" chip), this returns true for everyone.
                    currentFilter == null || row.courses.contains(currentFilter)
                }

            _uiState.value = _uiState.value.copy(
                results = rows,
                isLoading = false
            )
        }
    }
}