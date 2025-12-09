package com.smartcourse.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.smartcourse.auth.AuthViewModel

@Composable
fun RootNavigation(authViewModel: AuthViewModel) {

    // MUST BE REMEMBERED ONCE
    val navController = rememberNavController()

    // DO NOT depend on authViewModel in recomposition
    RootNavHost(
        navController = navController,
        authVM = authViewModel
    )
}
