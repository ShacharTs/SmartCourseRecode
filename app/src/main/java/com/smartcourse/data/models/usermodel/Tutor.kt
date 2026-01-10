package com.smartcourse.data.models.usermodel


data class Tutor(
    override val user: User,
    val teachingCourses: List<Course>,
    val savedStudentIds: Set<String> = emptySet(),
    val favoritesCount: Int = 0,
) : DomainUser by user




