package com.smartcourse.navigation

sealed class Screen(val route: String) {

    // Auth
    object Login : Screen("login")

    object Register : Screen("register")

    object ChooseRole : Screen("choose_role")


    object Loading : Screen("loading")

    object Home : Screen("home")



    object UserScreen : Screen("user_screen")


    object SearchRouter : Screen("search_router")


    object Profile : Screen("profile")


    // Chat
    object ChatList : Screen("chat_list")
    //object ChatTest : Screen("chat_test")
    object ChatRoom : Screen("chat/{chatId}")       // navigation param

    object Camera : Screen("camera")


}
