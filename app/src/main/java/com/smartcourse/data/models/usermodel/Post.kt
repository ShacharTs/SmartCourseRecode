package com.smartcourse.data.models.usermodel


data class Post(
    val id: String,
    val userId: String,
    val displayName: String = "Unknown",
    val content: String,
    val createdAt: String,
    val courses: List<Course>,
    val userRole: UserRole = UserRole.TEMP
)
