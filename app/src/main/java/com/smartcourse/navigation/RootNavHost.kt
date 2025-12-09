package com.smartcourse.navigation

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
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
    val state = authVM.authState
    Log.d("RootNavHost", "state: $state")

    // NEW: Navigate based on state
    LaunchedEffect(state) {
        when (state) {
            AuthState.LOGGED_OUT -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Loading.route) { inclusive = true }
                }
            }

            AuthState.REGISTERED -> {
                navController.navigate(Screen.ChooseRole.route) {
                    popUpTo(Screen.Loading.route) { inclusive = true }
                }
            }

            AuthState.LOGGED_IN -> {
                navController.navigate(Screen.UserRouter.route) {
                    popUpTo(Screen.Loading.route) { inclusive = true }
                }
            }

            AuthState.LOADING -> {
                // פשוט נשאר ב-LOADING
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Loading.route
    ) {

        // LOADING SCREEN
        composable(Screen.Loading.route) {
            Text("Loading...")
        }

        // LOGIN
        composable(Screen.Login.route) {
            LoginScreen(navController, authVM)
        }

        // REGISTER
        composable(Screen.Register.route) {
            RegisterScreen(navController, authVM)
        }

        // CHOOSE ROLE
        composable(Screen.ChooseRole.route) {
            ChooseRoleScreen(navController, authVM, hiltViewModel())
        }

        // USER ROUTER
        composable(Screen.UserRouter.route) {
            DummyReachedScreen()
        }
    }
}





