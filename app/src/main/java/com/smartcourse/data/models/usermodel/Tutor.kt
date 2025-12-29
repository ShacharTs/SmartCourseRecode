package com.smartcourse.data.models.usermodel

import com.smartcourse.data.models.usermodel.Course


data class Tutor(
    override val user: User,
    val teachingCourses: List<Course>,
    val savedStudentIds: Set<String> = emptySet()
) : DomainUser by user




