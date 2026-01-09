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
    val chat: ChatColorPalette,
    val nav: NavBarColorPalette,
    val isDark: Boolean
)

val DarkAppPalette = AppPalette(
    isDark = true,
    settings = SettingScreenColors.Dark,
    login = LoginScreenColors.Dark,
    register = RegisterScreenColors.Dark,
    home = StudentHomeLayoutColors.Dark,
    chat = ChatScreenColors.Dark,
    nav = NavBarColors.Dark
)

val LightAppPalette = AppPalette(
    isDark = false,
    settings = SettingScreenColors.Light,
    login = LoginScreenColors.Light,
    register = RegisterScreenColors.Light,
    home = StudentHomeLayoutColors.Light,
    chat = ChatScreenColors.Light,
    nav = NavBarColors.Light
)

val LocalAppPalette = staticCompositionLocalOf { DarkAppPalette }