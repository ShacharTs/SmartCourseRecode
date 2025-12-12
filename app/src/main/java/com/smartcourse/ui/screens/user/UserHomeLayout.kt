package com.smartcourse.ui.screens.user

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.ui.screens.components.CustomText
import com.smartcourse.ui.screens.user.student.StudentHomeLayout
import com.smartcourse.ui.screens.user.tutor.TutorHomeLayout


@Composable
fun UserHomeLayout(
    navController: NavController,
    authVM: AuthViewModel
) {
    val currentUser by authVM.currentUser.collectAsState()
    val role = currentUser?.getUserRole()

    when (role) {

        UserRole.STUDENT -> {
            StudentHomeLayout(
                navController = navController,
                authVM = authVM
            )
        }

        UserRole.TUTOR -> {
            TutorHomeLayout(
                navController = navController,
                authVM = authVM
            )
        }

        UserRole.ADMIN -> {
            CustomText("AdminHomeScreen")
        }

        UserRole.TEMP, null -> {
            // This should not normally happen
            CustomText("Invalid user state")
        }
    }
}










