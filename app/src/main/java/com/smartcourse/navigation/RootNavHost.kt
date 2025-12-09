package com.smartcourse.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.smartcourse.DummyReachedScreen
import com.smartcourse.auth.AuthState
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.ui.screens.chooserole.ChooseRoleScreen
import com.smartcourse.ui.screens.login.LoginScreen
import com.smartcourse.ui.screens.register.RegisterScreen
import com.smartcourse.viewmodels.ChooseRoleViewModel

@Composable
fun RootNavHost(
    navController: NavHostController,
    authVM: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Loading.route
    ) {

        composable(Screen.Loading.route) {

            val state by authVM::authState

            LaunchedEffect(state) {
                when (state) {
                    AuthState.LOGGED_OUT -> {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Loading.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }

                    AuthState.REGISTERED -> {
                        navController.navigate(Screen.ChooseRole.route) {
                            popUpTo(Screen.Loading.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }

                    AuthState.LOGGED_IN -> {
                            navController.navigate(Screen.UserRouter.route) {
                                popUpTo(Screen.Loading.route) { inclusive = true }
                                launchSingleTop = true

                        }
                    }

                    else -> Unit
                }
            }
        }



            // ************** AUTH SCREENS **************
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                authViewModel = authVM
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                authViewModel = authVM
            )
        }

        // ************** CHOOSE ROLE **************
        composable(Screen.ChooseRole.route) {
            val vm: ChooseRoleViewModel = hiltViewModel()
            ChooseRoleScreen(
                navController = navController,
                authViewModel = authVM,
                chooseRoleViewModel = vm
            )
        }

        // ************** MAIN USER ROUTER **************
        composable(Screen.UserRouter.route) {
            //UserRouterScreen(navController, authVM)
            DummyReachedScreen()
        }

//        composable(Screen.SearchRouter.route) {
//
//        }

//        composable(Screen.ChatList.route) {
//
//        }

//        composable(Screen.ChatRoom.route) { entry ->
//            val chatId = entry.arguments?.getString("chatId")!!
//        }

//        composable(Screen.Profile.route) {
//
//        }
    }
}
