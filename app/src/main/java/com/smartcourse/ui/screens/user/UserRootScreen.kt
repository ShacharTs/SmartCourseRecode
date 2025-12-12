package com.smartcourse.ui.screens.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
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
import com.smartcourse.ui.screens.chat.ChatListViewModel
import com.smartcourse.ui.screens.chat.ChatViewModel
import com.smartcourse.ui.screens.setting.SettingsScreen

@Composable
fun UserRootScreen(
    navController: NavHostController,
    authVM: AuthViewModel
) {
    val navController = rememberNavController()


    val currentUser by authVM.currentUser.collectAsState()
    val role = currentUser?.getUserRole() ?: UserRole.TEMP

    val items = bottomNavItemsForRole(role)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val topBarRoutes = setOf(
        Screen.Home.route
    )

    val bottomBarRoutes = setOf(
        Screen.Home.route,
        Screen.ChatList.route,
        Screen.SearchRouter.route,
        Screen.Profile.route
    )

    Scaffold(
        topBar = {
            if (currentRoute in topBarRoutes) {
                MenuTopAppBar(
                    navController = navController,
                    authVM = authVM
                )
            }
        },
        bottomBar = {
            if (currentRoute in bottomBarRoutes && items.isNotEmpty()) {
                AppBottomNavBar(
                    navController = navController,
                    items = items
                )
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {

            composable(Screen.Home.route) {
                MenuScreen(padding) {
                    ShowUserMenuScreen(
                        navController = navController,
                        authVM = authVM
                    )
                }
            }

            composable(Screen.ChatList.route) {
                val chatListVM = hiltViewModel<ChatListViewModel>()

                MenuScreen(padding) {
                    ChatListScreen(
                        navController = navController,
                        chatListVM = chatListVM
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
                val myId = authVM.currentUser.value?.getUID() ?: return@composable

                ChatScreen(
                    chatVM = chatVM,
                    navController = navController,
                    myId = myId
                )
            }

            composable(Screen.SearchRouter.route) {
                MenuScreen(padding) {
                    Text("Search Screen Content")
                }
            }

            composable(Screen.Profile.route) {
                MenuScreen(padding) {
                    Text("Profile Screen Content")
                }
            }


            composable(Screen.Settings.route){
                SettingsScreen(
                    navController = navController,
                    authVM = authVM
                )
            }
        }
    }
}

@Composable
fun MenuScreen(
    padding: PaddingValues,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.padding(padding)) {
        content()
    }
}

@Composable
private fun ShowUserMenuScreen(
    navController: NavController,
    authVM: AuthViewModel
) {
    val currentUser by authVM.currentUser.collectAsState()
    val role = currentUser?.getUserRole()

    when (role) {

        UserRole.STUDENT,
        UserRole.TUTOR -> {
            UserHomeLayout(
                navController = navController,
                authVM = authVM
            )
        }

        UserRole.ADMIN -> {
            CustomText("AdminHomeScreen")
        }

        null, UserRole.TEMP -> {
            // This should NEVER happen if navigation is correct
            CustomText("Invalid user state")
        }
    }
}

