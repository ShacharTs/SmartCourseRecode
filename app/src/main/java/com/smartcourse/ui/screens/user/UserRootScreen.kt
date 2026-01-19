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
import androidx.navigation.NavController
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
import com.smartcourse.ui.screens.chat.ChatScreen
import com.smartcourse.ui.screens.chat.vm.ChatViewModel
import com.smartcourse.ui.screens.chatlist.ChatListScreen
import com.smartcourse.ui.screens.chatlist.ChatListViewModel
import com.smartcourse.ui.screens.loading.LoadingScreen
import com.smartcourse.ui.screens.navbar.AppBottomNavBar
import com.smartcourse.ui.screens.navbar.MenuTopAppBar
import com.smartcourse.ui.screens.navbar.bottomNavItemsForRole
import com.smartcourse.ui.screens.search.SearchUserScreen
import com.smartcourse.ui.screens.search.SearchUserViewModel
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
    //authVM: AuthViewModel
) {
    val navController = rememberNavController()
    val authVM: AuthViewModel = hiltViewModel()


    val context = LocalContext.current
    val activity = context as? android.app.Activity
    val intent = activity?.intent

    val action = intent.getNotificationAction()

    LaunchedEffect(action) {
        when (action) {

            NotificationAction.CHAT_MESSAGE -> {
                val chatId = intent?.getStringExtra("CHAT_ID") ?: return@LaunchedEffect
                navController.navigate(
                    Screen.ChatRoom.createRoute(chatId)
                ) {
                    launchSingleTop = true
                }
            }

            // TEMP – valid example
            NotificationAction.USER_PROFILE -> {
                val userId = intent?.getStringExtra("USER_ID") ?: return@LaunchedEffect
                navController.navigate(
                    Screen.ShowOtherProfile.createRoute(userId)
                ) {
                    launchSingleTop = true
                }
            }

            NotificationAction.NONE -> Unit
        }

        // must clean – otherwise it will re-trigger
        intent?.removeExtra("NOTIFICATION_ACTION")
        intent?.removeExtra("CHAT_ID")
        intent?.removeExtra("USER_ID")
    }






    val palette = LocalAppPalette.current
    val isDark = palette.isDark

    val backgroundBrush = Brush.verticalGradient(
        colors = if (isDark) AppGradients.Dark else AppGradients.Light
    )

    //val currentUser by authVM.currentUser.collectAsState()
    //val role = currentUser?.role ?: UserRole.TEMP

    val userProfile = authVM.currentUserProfile
    val role = userProfile?.role ?: UserRole.TEMP

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

    Scaffold(containerColor = Color.Transparent, topBar = {
        if (currentRoute in topBarRoutes) {
            MenuTopAppBar(
                navController = navController, authVM = authVM
            )
        }
    }, bottomBar = {
        if (currentRoute in bottomBarRoutes && items.isNotEmpty()) {
            AppBottomNavBar(
                navController = navController, items = items
            )
        }
    }) { padding ->

        //  SINGLE OWNER OF WINDOW BACKGROUND
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.fillMaxSize()
                //.padding(padding)
            ) {

                composable(Screen.Home.route) {
                    MenuScreen(padding) {
                        ShowUserMenuScreen(
                            navController = navController, authVM = authVM
                        )
                    }
                }

                composable(Screen.ChatList.route) {
                    val chatListVM = hiltViewModel<ChatListViewModel>()

                    MenuScreen(padding) {
                        ChatListScreen(
                            navController = navController, chatListVM = chatListVM
                        )
                    }
                }



                composable(
                    route = Screen.ChatRoom.route,
                    arguments = listOf(
                        navArgument("chatId") { type = NavType.StringType }
                    )
                ) { entry ->

                    val chatId = entry.arguments?.getString("chatId")
                        ?: error("chatId missing")

                    val chatVM: ChatViewModel = hiltViewModel(entry)

                    //val myId = authVM.currentUser.value?.getUID()

//                    val myId = authVM.currentUser.value?.userId
//                        ?: return@composable

                    val myId = authVM.currentUserProfile?.userId
                        ?: return@composable

                    ChatScreen(
                        chatVM = chatVM,
                        navController = navController,
                        myId = myId,
                        chatId = chatId
                    )
                }


                composable(Screen.SearchRouter.route) {
                    val searchUserVM: SearchUserViewModel = hiltViewModel()

                    SearchUserScreen(
                        navController = navController,
                        viewModel = searchUserVM
                    )
                }


                composable(Screen.Profile.route) {
                    UserProfileScreen(
                        navController = navController,
                        authVM = authVM

                    )
                }


                composable(Screen.Settings.route) {
                    SettingsScreen(
                        navController = navController, authVM = authVM
                    )
                }

                composable(
                    route = Screen.ShowOtherProfile.route,
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) {
                    ShowOtherProfileScreen(
                        navController = navController
                    )
                }

                composable(Screen.Theme.route){
                    ThemeScreen(navController = navController)
                }

                composable(Screen.Language.route){
                    LanguageScreen(navController = navController)
                }


                composable(Screen.Terms.route) {
                    TermsAndServiceScreen(
                        showBackButton = true,
                        requireAcceptance = false,
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }








            }
        }
    }
}


@Composable
fun MenuScreen(
    padding: PaddingValues, content: @Composable () -> Unit
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
    //when (val user = authVM.domainUser) {
    val user = authVM.currentUserProfile

    when {
        user == null -> LoadingScreen()

        user.role == UserRole.STUDENT -> StudentHomeLayout(
            navController = navController,
            user = user // Pass the flat User object
        )

        user.role == UserRole.TUTOR -> TutorHomeLayout(
            navController = navController,
            user = user // Pass the flat User object
        )

        else -> LoadingScreen() // Handle TEMP or unexpected roles
    }
}

fun Intent?.getNotificationAction(): NotificationAction {
    return when (this?.getStringExtra("NOTIFICATION_ACTION")) {
        "CHAT_MESSAGE" -> NotificationAction.CHAT_MESSAGE
        "USER_PROFILE" -> NotificationAction.USER_PROFILE
        else -> NotificationAction.NONE
    }
}
