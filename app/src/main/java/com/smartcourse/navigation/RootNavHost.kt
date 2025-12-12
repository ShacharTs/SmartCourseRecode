package com.smartcourse.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.smartcourse.auth.AuthState
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.ui.screens.login.LoginViewModel
import com.smartcourse.ui.screens.chooserole.ChooseRoleScreen
import com.smartcourse.ui.screens.login.LoginScreen
import com.smartcourse.ui.screens.register.RegisterScreen
import com.smartcourse.ui.screens.user.UserRootScreen
import com.smartcourse.ui.screens.chooserole.ChooseRoleViewModel
import com.smartcourse.ui.screens.register.RegisterViewModel

@Composable
fun RootNavHost(
    navController: NavHostController,
    authVM: AuthViewModel
) {
    val state = authVM.authState

    LaunchedEffectStates(
        state = state,
        navController = navController
    )

    NavHostGraph(
        navController = navController,
        authVM = authVM
    )
}


@Composable
private fun LaunchedEffectStates(
    state: AuthState,
    navController: NavHostController
) {

    LaunchedEffect(state) {
        when (state) {

            AuthState.LOGGED_OUT -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }

            AuthState.REGISTERING -> {
                navController.navigate(Screen.Register.route) {
                    popUpTo(Screen.Login.route) { inclusive = false }
                    launchSingleTop = true
                }
            }

            AuthState.CHOOSING_ROLE -> {
                navController.navigate(Screen.ChooseRole.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }

            AuthState.LOGGED_IN -> {
                navController.navigate(Screen.UserScreen.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }

            AuthState.LOADING -> Unit
        }
    }
}



@Composable
private fun NavHostGraph(
    navController: NavHostController,
    authVM: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Loading.route
    ) {

        composable(Screen.Loading.route) {
            Text("Loading...")
        }

        composable(Screen.Login.route) {
            val loginVM = hiltViewModel<LoginViewModel>()

            LoginScreen(
                navController = navController,
                loginVM = loginVM,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            val registerVM = hiltViewModel<RegisterViewModel>()
            RegisterScreen(
                registerVM = registerVM,
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.ChooseRole.route)
                }
            )
        }

        composable(Screen.ChooseRole.route) {
            val chooseRoleVM = hiltViewModel<ChooseRoleViewModel>()
            ChooseRoleScreen(chooseRoleViewModel = chooseRoleVM, authVM = authVM)
        }

        composable(Screen.UserScreen.route) {
            UserRootScreen(
                navController = navController,
                authVM = authVM)
        }
    }
}









