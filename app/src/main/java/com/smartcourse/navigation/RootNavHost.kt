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
import com.smartcourse.ui.screens.user.UserMenuScreen
import com.smartcourse.ui.screens.user.UserMenuScreenNew
import com.smartcourse.viewmodels.ChooseRoleViewModel

@Composable
fun RootNavHost(
    navController: NavHostController,
    authVM: AuthViewModel
) {
    val state = authVM.authState
    Log.d("RootNavHost", "state: $state")

    LaunchedEffect(state) {
        when (state) {
            AuthState.LOGGED_OUT -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }

            AuthState.REGISTERED -> {
                navController.navigate(Screen.ChooseRole.route) {
                    popUpTo(0) { inclusive = true }
                }
            }

            AuthState.LOGGED_IN -> {
                navController.navigate(Screen.UserRouter.route) {
                    popUpTo(0) { inclusive = true }
                }
            }

            AuthState.LOADING -> {}
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Loading.route
    ) {

        composable(Screen.Loading.route) {
            Text("Loading...")
        }

        composable(Screen.Login.route) {
            LoginScreen(navController, authVM)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController, authVM)
        }

        composable(Screen.ChooseRole.route) {
            ChooseRoleScreen(navController, authVM, hiltViewModel())
        }

        composable(Screen.UserRouter.route) {
            UserMenuScreenNew(
                navController = navController,
                authVM = authVM
            )
            //DummyReachedScreen(navController, authVM)
        }
    }
}






