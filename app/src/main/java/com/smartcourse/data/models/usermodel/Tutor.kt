package com.smartcourse.data.models.usermodel



data class Tutor(
    override val user: User,
    val teachingCourses: List<Course>,
    val savedStudentIds: Set<String> = emptySet()
) : DomainUser() {



}
