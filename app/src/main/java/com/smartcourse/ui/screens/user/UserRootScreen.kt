package com.smartcourse.ui.screens.user

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.navigation.Screen
import com.smartcourse.notifications.NotificationAction
import com.smartcourse.ui.screens.chat.screen.ChatScreen
import com.smartcourse.ui.screens.chatlist.ChatListScreen
import com.smartcourse.ui.screens.loading.LoadingScreen
import com.smartcourse.ui.screens.navbar.AppBottomNavBar
import com.smartcourse.ui.screens.navbar.MenuTopAppBar
import com.smartcourse.ui.screens.navbar.bottomNavItemsForRole
import com.smartcourse.ui.screens.search.SearchUserScreen
import com.smartcourse.ui.screens.setting.SettingsScreen
import com.smartcourse.ui.screens.setting.language.LanguageScreen
import com.smartcourse.ui.screens.setting.terms.TermsAndServiceScreen
import com.smartcourse.ui.screens.setting.theme.ThemeScreen
import com.smartcourse.ui.screens.user.profile.UserProfileScreen
import com.smartcourse.ui.screens.user.profile.showother.ShowOtherProfileScreen
import com.smartcourse.ui.screens.user.student.StudentHomeLayout
import com.smartcourse.ui.screens.user.tutor.TutorHomeLayout
import com.smartcourse.ui.theme.AppGradients
import com.smartcourse.ui.theme.LocalAppPalette

@Composable
fun UserRootScreen(
    navController: NavHostController = rememberNavController()
) {
    val internalNavController = rememberNavController()
    val authVM: AuthViewModel = hiltViewModel()


    NotificationHandler(internalNavController)

    val palette = LocalAppPalette.current
    val backgroundBrush = Brush.verticalGradient(
        colors = if (palette.isDark) AppGradients.Dark else AppGradients.Light
    )

    val navBackStackEntry by internalNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val userRole = authVM.currentUserProfile?.role ?: UserRole.TEMP

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            UserTopBar(currentRoute, internalNavController, authVM)
        },
        bottomBar = {
            UserBottomBar(currentRoute, internalNavController, userRole)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                //.fillMaxSize()
                .background(backgroundBrush)
        ) {
            UserNavGraph(
                internalNavController = internalNavController,
                authVM = authVM,
                padding = padding
            )
        }
    }
}

@Composable
private fun NotificationHandler(navController: NavHostController) {
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    val intent = activity?.intent
    val action = intent.getNotificationAction()

    LaunchedEffect(action) {
        when (action) {
            NotificationAction.CHAT_MESSAGE -> {
                intent?.getStringExtra("CHAT_ID")?.let { chatId ->
                    navController.navigate(Screen.ChatRoom.createRoute(chatId)) {
                        launchSingleTop = true
                    }
                }
            }
            NotificationAction.USER_PROFILE -> {
                intent?.getStringExtra("USER_ID")?.let { userId ->
                    navController.navigate(Screen.ShowOtherProfile.createRoute(userId)) {
                        launchSingleTop = true
                    }
                }
            }
            NotificationAction.NONE -> Unit
        }

        intent?.removeExtra("NOTIFICATION_ACTION")
        intent?.removeExtra("CHAT_ID")
        intent?.removeExtra("USER_ID")
    }
}

@Composable
private fun UserTopBar(
    currentRoute: String?,
    navController: NavHostController,
    authVM: AuthViewModel
) {
    val topBarRoutes = setOf(Screen.Home.route)
    if (currentRoute in topBarRoutes) {
        MenuTopAppBar(navController = navController, authVM = authVM)
    }
}

@Composable
private fun UserBottomBar(
    currentRoute: String?,
    navController: NavHostController,
    role: UserRole
) {
    val bottomBarRoutes = setOf(
        Screen.Home.route,
        Screen.ChatList.route,
        Screen.SearchRouter.route,
    )
    val items = bottomNavItemsForRole(role)

    if (currentRoute in bottomBarRoutes && items.isNotEmpty()) {
        AppBottomNavBar(navController = navController, items = items)
    }
}

@Composable
private fun UserNavGraph(
    internalNavController: NavHostController,
    authVM: AuthViewModel,
    padding: PaddingValues
) {
    NavHost(
        navController = internalNavController,
        startDestination = Screen.Home.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.Home.route) {
            MenuScreen(padding) {
                ShowHomeScreen(navController = internalNavController, authVM = authVM)
            }
        }

        composable(Screen.ChatList.route) {
            MenuScreen(padding) {
                ChatListScreen(navController = internalNavController)
            }
        }

        composable(
            route = Screen.ChatRoom.route,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { entry ->
            val chatId = entry.arguments?.getString("chatId") ?: return@composable
            val myId = authVM.currentUserProfile?.userId ?: return@composable

            ChatScreen(
                navController = internalNavController,
                myId = myId,
                chatId = chatId
            )
        }

        composable(Screen.SearchRouter.route) {
            SearchUserScreen(navController = internalNavController)
        }

        composable(Screen.Profile.route) {
            UserProfileScreen(navController = internalNavController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = internalNavController)
        }

        composable(
            route = Screen.ShowOtherProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            ShowOtherProfileScreen(navController = internalNavController)
        }

        composable(Screen.Theme.route) {
            ThemeScreen(navController = internalNavController)
        }

        composable(Screen.Language.route) {
            LanguageScreen(navController = internalNavController)
        }

        composable(Screen.Terms.route) {
            TermsAndServiceScreen(
                showBackButton = true,
                requireAcceptance = false,
                onBack = { internalNavController.popBackStack() }
            )
        }
    }
}

/**
 * Helper composable for show home ,chatlist and search
 */
@Composable
fun MenuScreen(
    padding: PaddingValues,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.padding(padding)) {
        content()
    }
}

/**
 * Show the correct home screen for the user role
 */
@Composable
fun ShowHomeScreen(
    navController: NavHostController,
    authVM: AuthViewModel
) {
    val user = authVM.currentUserProfile
    when {
        user == null -> LoadingScreen()
        user.role == UserRole.STUDENT -> StudentHomeLayout(navController = navController)
        user.role == UserRole.TUTOR -> TutorHomeLayout(navController = navController)
        else -> LoadingScreen()
    }
}

fun Intent?.getNotificationAction(): NotificationAction {
    return when (this?.getStringExtra("NOTIFICATION_ACTION")) {
        "CHAT_MESSAGE" -> NotificationAction.CHAT_MESSAGE
        "USER_PROFILE" -> NotificationAction.USER_PROFILE
        else -> NotificationAction.NONE
    }
}