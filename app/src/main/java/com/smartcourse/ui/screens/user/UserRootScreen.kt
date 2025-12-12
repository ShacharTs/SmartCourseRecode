package com.smartcourse.ui.screens.user

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.screens.components.CustomText
import com.smartcourse.ui.screens.navbar.AppBottomNavBar
import com.smartcourse.ui.screens.navbar.MenuTopAppBar
import com.smartcourse.ui.screens.navbar.bottomNavItemsForRole


@Composable
fun UserRootScreen(authVM: AuthViewModel) {
    val navController = rememberNavController()

    val role = authVM.user?.getUserRole()
    val items = bottomNavItemsForRole(role ?: UserRole.TEMP)

    // Observe current inner route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

     val topBarRoutes = setOf(
        Screen.Home.route,
    )

    val showTopBar = currentRoute in topBarRoutes


    Scaffold(
        topBar = {
            if (showTopBar) {
                MenuTopAppBar(
                    navController = navController,
                    authVM = authVM
                )
            }
        },
        bottomBar = {
            if (items.isNotEmpty()) {
                AppBottomNavBar(
                    navController = navController,
                    items = items
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {

            composable(Screen.Home.route) {
                ShowUserMenuScreen(authVM)
            }


            composable(Screen.ChatList.route) {
                Text("Chat List Screen Content")
            }

            composable(Screen.SearchRouter.route) {
                Text("Search Screen Content")
            }

            composable(Screen.Profile.route) {
                Text("Profile Screen Content")
            }
        }
    }
}

@Composable
private fun ShowUserMenuScreen(authVM: AuthViewModel) {
    when (authVM.user?.role) {

        UserRole.STUDENT -> {
            //StudentHomeScreen(navController, authVM)
            CustomText("StudentHomeScreen")
        }

        UserRole.TUTOR -> {
            //TutorHomeScreen(navController, authVM)
            CustomText("TutorHomeScreen")
        }

        UserRole.ADMIN -> {
            //AdminHomeScreen(navController, authVM)
            CustomText("AdminHomeScreen")
        }

        else -> {
            // TEMP / null safety
            //DummyReachedScreen(navController, authVM)
            CustomText("DummyReachedScreen")
        }
    }
}
