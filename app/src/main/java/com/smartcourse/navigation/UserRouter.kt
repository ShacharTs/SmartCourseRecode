package com.smartcourse.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.UserRole


@Composable
fun UserRouterScreen(
    navController: NavHostController,
    authVM: AuthViewModel
) {
    val user = authVM.user

    if (user == null) {
        // load data

        //LoadingScreen()
        return
    }

    when (user.role) {

        UserRole.STUDENT -> {
            //StudentMainScreen(navController, authVM)
        }

        UserRole.TUTOR -> {
            //TutorMainScreen(navController, authVM)
        }

        UserRole.ADMIN -> {

        }

        UserRole.TEMP, null -> {
            navController.navigate(Screen.ChooseRole.route) {
                popUpTo(Screen.UserRouter.route) { inclusive = true }
            }
        }
    }
}
