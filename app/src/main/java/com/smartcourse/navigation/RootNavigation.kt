package com.smartcourse.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.smartcourse.auth.AuthViewModel


@Composable
fun RootNavigation(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    RootNavHost(navController = navController, authVM = authViewModel)
}
