package com.smartcourse.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartcourse.auth.AuthState
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.ui.screens.chooserole.ChooseRoleScreen
import com.smartcourse.ui.screens.loading.LoadingScreen
import com.smartcourse.ui.screens.login.LoginScreen
import com.smartcourse.ui.screens.register.RegisterScreen
import com.smartcourse.ui.screens.setting.AppStartViewModel
import com.smartcourse.ui.screens.setting.terms.TermsAndServiceScreen
import com.smartcourse.ui.screens.user.UserRootScreen

@Composable
fun RootNavHost() {
    val navController = rememberNavController()

    val authVM: AuthViewModel = hiltViewModel()
    val state = authVM.authState

    LaunchedEffectStates(
        state = state,
        navController = navController
    )

    NavHostGraph(navController = navController)
}


@Composable
private fun LaunchedEffectStates(
    state: AuthState,
    navController: NavHostController
) {

    LaunchedEffect(state) {
        when (state) {

            AuthState.TERMS -> {
                navController.navigate(Screen.TermsFirstTime.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }

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

            AuthState.LOADING -> {
                navController.navigate(Screen.Loading.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            }



        }
    }
}



@Composable
private fun NavHostGraph(
    navController: NavHostController,
    appStartViewModel: AppStartViewModel = hiltViewModel()
) {
    val termsAccepted by appStartViewModel.termsAccepted.collectAsState()

    val startDestination = when {
        !termsAccepted -> Screen.TermsFirstTime.route
        else -> Screen.Loading.route
    }


    NavHost(
        navController = navController,
        startDestination = Screen.Loading.route
    ) {
        composable(Screen.TermsFirstTime.route) {
            TermsAndServiceScreen(
                showBackButton = false,
                requireAcceptance = true,
                onAccepted = {
                    appStartViewModel.acceptTerms()

                    navController.navigate(Screen.Loading.route) {
                        popUpTo(Screen.TermsFirstTime.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }


        composable(Screen.Loading.route) {
            LoadingScreen()
        }


        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.ChooseRole.route)
                }
            )
        }

        composable(Screen.ChooseRole.route) {
            ChooseRoleScreen(navController = navController)
        }

        composable(Screen.UserScreen.route) {
            UserRootScreen(navController = navController)
        }

    }
}
