package com.smartcourse.data.models.usermodel

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.ui.screens.user.tutor.TutorHomeLayout


class Tutor(
    override val user: User,
    val teachingCourses: List<Course>,
    val savedStudentIds: Set<String> = emptySet()
) : DomainUser() {


}

