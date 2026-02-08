package com.smartcourse.ui.screens.search

data class SearchUserUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<SearchUserRowState> = emptyList(),
    val availableCourses: List<String> = emptyList(),
    val selectedCourse: String? = null
)
