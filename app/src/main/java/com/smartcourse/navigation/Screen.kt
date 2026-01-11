package com.smartcourse.navigation

sealed class Screen(val route: String) {

    // Auth
    object Login : Screen("login")

    object Register : Screen("register")

    object ChooseRole : Screen("choose_role")


    object Loading : Screen("loading")

    object Home : Screen("home")

    object Settings : Screen("settings")




    object UserScreen : Screen("user_screen")


    object SearchRouter : Screen("search_router")


    object Profile : Screen("profile")

    object ShowOtherProfile : Screen("show_other_profile/{userId}") {
        fun createRoute(userId: String): String =
            "show_other_profile/$userId"
    }


    object Theme : Screen("theme")

    object Language : Screen("language")

    object TermsFirstTime : Screen("terms_first_time")

    object Terms : Screen("terms")



    // Chat
    object ChatList : Screen("chat_list")

    object ChatRoom : Screen("chat/{chatId}") {
        fun createRoute(chatId: String) = "chat/$chatId"
    }



    object Camera : Screen("camera")



}
