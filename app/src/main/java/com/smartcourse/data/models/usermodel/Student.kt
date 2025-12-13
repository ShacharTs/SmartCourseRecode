package com.smartcourse.data.models.usermodel

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.ui.screens.user.student.StudentHomeLayout


data class Student(
    override val user: User,
    val coursesSeekingHelp: List<Course>,
    val savedTutorIds: Set<String> = emptySet()
) : DomainUser() {

}