package com.smartcourse.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.Student
import com.smartcourse.data.models.usermodel.Tutor
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
import com.smartcourse.ui.screens.user.student.StudentHomeLayout
import com.smartcourse.ui.screens.user.tutor.TutorHomeLayout
import com.smartcourse.ui.theme.AppGradients
import com.smartcourse.ui.theme.LocalAppPalette

@Composable
fun UserRootScreen(
    navController: NavHostController,
    authVM: AuthViewModel
) {
    val navController = rememberNavController()
    val palette = LocalAppPalette.current
    val isDark = palette.isDark

    val backgroundBrush = Brush.verticalGradient(
        colors = if (isDark) AppGradients.Dark else AppGradients.Light
    )

    val currentUser by authVM.currentUser.collectAsState()
    val role = currentUser?.role ?: UserRole.TEMP

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
    )

    Scaffold(
        containerColor = Color.Transparent,
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

        //  SINGLE OWNER OF WINDOW BACKGROUND
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier
                    .fillMaxSize()
                //.padding(padding)
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
                        Box(
                            Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = currentRoute.toString(),
                                color = Color.White,
                                )
                        }

                    }
                }

                composable(Screen.Profile.route) {
                    UserProfileScreen(
                        navController = navController,
                        authVM = authVM
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        navController = navController,
                        authVM = authVM
                    )
                }
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
fun ShowUserMenuScreen(
    navController: NavController,
    authVM: AuthViewModel
) {
    when (val user = authVM.domainUser) {
        null -> CustomText("Loading...")

        is Student -> StudentHomeLayout(
            navController = navController,
            student = user
        )

        is Tutor -> TutorHomeLayout(
            navController = navController,
            tutor = user
        )
    }
}
