package com.smartcourse.ui.screens.search

import com.smartcourse.data.models.usermodel.User

// UI-only row state (NOT domain, NOT repository)
data class SearchUserRowState(
    val user: User,
    val courses: List<String>
)
