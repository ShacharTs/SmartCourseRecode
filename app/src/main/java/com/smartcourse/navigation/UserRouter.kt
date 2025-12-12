//package com.smartcourse.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavHostController
//import com.smartcourse.auth.AuthViewModel
//import com.smartcourse.data.models.usermodel.UserRole
//import com.smartcourse.ui.screens.user.UserMenuScreen
//import com.smartcourse.viewmodels.StudentViewModel
//import com.smartcourse.viewmodels.TutorViewModel
//
//
//@Composable
//fun UserRouterScreen(
//    navController: NavHostController,
//    authVM: AuthViewModel
//) {
//    val user = authVM.user
//
//    if (user == null) {
//        // load data
//
//        //LoadingScreen()
//        return
//    }
//
//    when (user.role) {
//
//        UserRole.STUDENT -> {
//            val studentVM : StudentViewModel = hiltViewModel()
//            UserMenuScreen(navController = navController, authVM = authVM, role = UserRole.STUDENT, studentVM = studentVM, tutorVM = null)
//        }
//
//        UserRole.TUTOR -> {
//            //TutorMainScreen(navController, authVM)
//            val tutorVM : TutorViewModel = hiltViewModel()
//            UserMenuScreen(navController = navController, authVM = authVM, role = UserRole.TUTOR, studentVM = null, tutorVM = tutorVM)
//        }
//
//        UserRole.ADMIN -> {
//
//        }
//
//        UserRole.TEMP, null -> {
//            navController.navigate(Screen.ChooseRole.route) {
//                popUpTo(Screen.UserRouter.route) { inclusive = true }
//            }
//        }
//    }
//}
