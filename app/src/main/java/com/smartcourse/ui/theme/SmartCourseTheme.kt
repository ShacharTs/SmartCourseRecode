package com.smartcourse.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color



private val LightColors = lightColorScheme(

    primary = Color(0xFF6750A4),
    onPrimary = Color.White,

    secondary = Color(0xFF625B71),
    onSecondary = Color.White,

    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),

    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),

    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),

    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),

    inverseSurface = Color(0xFFF4EFF4),
    surfaceTint = Color(0xFF6750A4),

    surfaceContainer = Color(0xFFF3EDF7),
    surfaceContainerLow = Color(0xFFF7F2FA),
)



private val DarkColors = darkColorScheme(

    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),

    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),

    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),

    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),

    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),

    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),

    inverseSurface = Color(0xFFE6E1E5),
    surfaceTint = Color(0xFFD0BCFF),

    surfaceContainer = Color(0xFF1C1B1F),
    surfaceContainerLow = Color(0xFF141218),
)




@Composable
fun SmartCourseTheme(content: @Composable () -> Unit) {

    //val vm = LocalSensorViewModel.current

    val systemDark = isSystemInDarkTheme()
    //val sensorDark = vm?.isDark == true


    //val useDark = if (systemDark) true else sensorDark
    val useDark = systemDark



    MaterialTheme(
        colorScheme = if (useDark) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}






