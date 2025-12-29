package com.smartcourse.data.models.table

import kotlinx.serialization.Serializable

@Serializable
data class UserCourseTable(
    val user_id: String,
    val course_id: String
)

