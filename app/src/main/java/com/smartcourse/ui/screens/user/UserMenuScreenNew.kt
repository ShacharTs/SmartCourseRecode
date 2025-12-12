package com.smartcourse.ui.screens.user

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.screens.navbar.AppBottomNavBar
import com.smartcourse.ui.screens.navbar.MenuTopAppBar
import com.smartcourse.ui.screens.navbar.bottomNavItemsForRole


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMenuScreenNew(
    navController: NavController, // Main NavController (used for global actions like Logout/Settings)
    authVM: AuthViewModel
) {
    // 1. Define the inner NavController for the Bottom Bar navigation
    val innerNavController = rememberNavController()

    // Get the current route of the inner NavHost
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentInnerRoute = navBackStackEntry?.destination?.route

    val userRole = authVM.user?.getUserRole()
    val bottomNavItems = if (userRole != null) {
        bottomNavItemsForRole(userRole)
    } else {
        emptyList()
    }

    // Define the condition for showing the full Top Bar (only on Home screen route)
    val isFullTopBarVisible = currentInnerRoute == Screen.UserRouter.route

    Scaffold(
        topBar = {
            // Conditional Top Bar display: Show the full bar only on the Home screen
            if (isFullTopBarVisible) {
                MenuTopAppBar(navController, authVM)
            }
            // If the condition is false, the topBar composable emits nothing, hiding the bar completely.
        },
        bottomBar = {
            // Bottom Bar remains visible always, uses the inner NavController
            AppBottomNavBar(
                navController = innerNavController,
                items = bottomNavItems
            )
        }
    ) { paddingValues ->

        // The main content area is the Nested NavHost
        NavHost(
            navController = innerNavController,
            startDestination = bottomNavItems.firstOrNull()?.route ?: "default_home",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply Scaffold padding
        ) {

            // Define all Bottom Bar composable destinations

            composable(Screen.UserRouter.route) {
                // Home Screen Content (where the Top Bar is visible)
                Text("Home Screen Content (UserRouter)")
            }

            composable(Screen.ChatList.route) {
                // Chat List Screen Content (Top Bar hidden)
                Text("Chat List Screen Content")
            }

            composable(Screen.SearchRouter.route) {
                // Search Screen Content (Top Bar hidden)
                Text("Search Screen Content")
            }

            composable(Screen.Profile.route) {
                // Profile Screen Content (Top Bar hidden)
                Text("Profile Screen Content")
            }
        }
    }
}

