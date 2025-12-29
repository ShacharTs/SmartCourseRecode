package com.smartcourse.data.models.usermodel

import com.smartcourse.data.models.usermodel.Course


data class Student(
    override val user: User,
    val coursesSeekingHelp: List<Course> = emptyList(),
    val savedTutorIds: Set<String> = emptySet()
) : DomainUser by user