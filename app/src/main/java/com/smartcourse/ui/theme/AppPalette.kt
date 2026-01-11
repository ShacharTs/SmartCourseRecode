package com.smartcourse.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf


/**
 * Your existing Master Palette setup
 */
data class AppPalette(
    val settings: SettingScreenColorPalette,
    val login: LoginColorPalette,
    val register: RegisterColorPalette,
    val home: StudentHomeColorPalette,
    val chatList: ChatListPalette,
    val chatRoom: ChatRoomPalette,
    val search: SearchColorPalette,
    val otherProfile : ShowOtherProfileColorPalette,
    val theme : ThemeScreenColorPalette,
    val nav: NavBarColorPalette,
    val isDark: Boolean
)

val DarkAppPalette = AppPalette(
    isDark = true,
    settings = SettingScreenColors.Dark,
    login = LoginScreenColors.Dark,
    register = RegisterScreenColors.Dark,
    home = StudentHomeLayoutColors.Dark,
    chatList = ChatListLayoutColors.Dark,
    chatRoom = ChatRoomLayoutColors.Dark,
    search = SearchScreenLayoutColors.Dark,
    otherProfile = ShowOtherProfileLayoutColors.Dark,
    theme = ThemeScreenColors.Dark,
    nav = NavBarColors.Dark
)

val LightAppPalette = AppPalette(
    isDark = false,
    settings = SettingScreenColors.Light,
    login = LoginScreenColors.Light,
    register = RegisterScreenColors.Light,
    home = StudentHomeLayoutColors.Light,
    chatList = ChatListLayoutColors.Light,
    chatRoom = ChatRoomLayoutColors.Light,
    search = SearchScreenLayoutColors.Light,
    otherProfile = ShowOtherProfileLayoutColors.Light,
    theme = ThemeScreenColors.Light,
    nav = NavBarColors.Light
)

val LocalAppPalette = staticCompositionLocalOf { DarkAppPalette }