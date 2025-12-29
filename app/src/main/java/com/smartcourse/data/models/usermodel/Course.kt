package com.smartcourse.data.models.usermodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Course(
    @SerialName("id")
    val id: String,

    @SerialName("course_name")
    val name: String
)