package com.smartcourse.ui.screens.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.screens.chat.ChatListScreen
import com.smartcourse.ui.screens.chat.ChatScreen
import com.smartcourse.ui.screens.components.CustomText
import com.smartcourse.ui.screens.navbar.AppBottomNavBar
import com.smartcourse.ui.screens.navbar.MenuTopAppBar
import com.smartcourse.ui.screens.navbar.bottomNavItemsForRole
import com.smartcourse.viewmodels.ChatViewModel


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
        ) {

            composable(Screen.Home.route) {
                Box(modifier = Modifier.padding(padding)) {
                    ShowUserMenuScreen(authVM)
                }
            }


            composable(Screen.ChatList.route) {
                Box(modifier = Modifier.padding(padding)) {
                    ChatListScreen(
                        navController = navController,
                        authVM = authVM,
                        chatListVM = hiltViewModel()
                    )
                }
            }


            composable(
                route = Screen.ChatRoom.route,
                arguments = listOf(
                    navArgument("chatId") { type = NavType.StringType }
                )
            ) { entry ->
                val chatVM: ChatViewModel = hiltViewModel(entry)

                ChatScreen(
                    chatVM = chatVM,
                    authVM = authVM,
                    navController = navController
                )
            }



            composable(Screen.SearchRouter.route) {
                Box(modifier = Modifier.padding(padding)) {
                    Text("Search Screen Content")
                }

            }

            composable(Screen.Profile.route) {
                Box(modifier = Modifier.padding(padding)) {
                    Text("Profile Screen Content")
                }
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
