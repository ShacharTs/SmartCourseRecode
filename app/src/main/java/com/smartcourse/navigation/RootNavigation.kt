package com.smartcourse.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun RootNavigation(
    //navController: NavHostController
    //authViewModel: AuthViewModel
) {

    // MUST BE REMEMBERED ONCE
    val navController = rememberNavController()

    // DO NOT depend on authViewModel in recomposition
    RootNavHost(
        navController = navController,
        //authVM = authViewModel
    )
}
