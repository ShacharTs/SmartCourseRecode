package com.smartcourse.ui.screens.search

data class SearchUserUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<SearchUserRowState> = emptyList()
)
