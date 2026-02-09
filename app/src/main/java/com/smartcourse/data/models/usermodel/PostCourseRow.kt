package com.smartcourse.data.models.usermodel

import kotlinx.serialization.Serializable

@Serializable
data class PostWithCourseRow(
    val post_id: String,
    val user_id: String,
    val content: String,
    val created_at: String,
    val course_id: String,
    val course_name: String
)

