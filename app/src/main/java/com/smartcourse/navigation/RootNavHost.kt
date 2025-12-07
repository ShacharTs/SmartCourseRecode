package com.smartcourse.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.smartcourse.auth.AuthState
import com.smartcourse.auth.AuthViewModel


@Composable
fun RootNavHost(
    navController: NavHostController,
    authVM: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "decider"
    ) {

        // ----------------------------------------------------
        // DECIDER (runs once, chooses correct route)
        // ----------------------------------------------------
        composable("decider") {

            val state = authVM.authState

            LaunchedEffect(state) {
                when (state) {
                    AuthState.LOGGED_OUT -> {
                        navController.navigate(Screen.Login.route) {
                            popUpTo("decider") { inclusive = true }
                        }
                    }

                    AuthState.REGISTERED -> {
                        navController.navigate(Screen.ChooseRole.route) {
                            popUpTo("decider") { inclusive = true }
                        }
                    }

                    AuthState.LOGGED_IN -> {
                        navController.navigate(Screen.UserRouter.route) {
                            popUpTo("decider") { inclusive = true }
                        }
                    }

                    else -> {}
                }
            }
        }

        // ----------------------------------------------------
        // AUTH SCREENS
        // ----------------------------------------------------
        composable(Screen.Login.route) {
            //LoginScreen(navController, authVM)
        }

        composable(Screen.Register.route) {
            //RegisterScreen(navController, authVM)
        }

        composable(Screen.ChooseRole.route) {
            //ChooseRoleScreen(navController, authVM)
        }

        // ----------------------------------------------------
        // MAIN APP SCREENS
        // ----------------------------------------------------
        composable(Screen.UserRouter.route) {
            //UserScreenFactory(navController, authVM)
        }

        composable(Screen.SearchRouter.route) {
            //SearchScreenFactory(navController, authVM)
        }

        composable(Screen.ChatList.route) {
            //ChatListScreen(navController, authVM)
        }

        composable(Screen.ChatRoom.route) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId")!!
            //ChatScreen(navController, chatId, authVM)
        }

        composable(Screen.Profile.route) {
            //ProfileScreen(navController, authVM)
        }
    }
}
