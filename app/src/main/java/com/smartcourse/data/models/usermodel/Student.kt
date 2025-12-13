package com.smartcourse.data.models.usermodel



data class Student(
    override val user: User,
    val coursesSeekingHelp: List<Course>,
    val savedTutorIds: Set<String> = emptySet()
) : DomainUser() {


}