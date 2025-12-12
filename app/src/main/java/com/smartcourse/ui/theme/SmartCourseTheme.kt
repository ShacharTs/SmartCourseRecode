package com.smartcourse.ui.theme


// Import the custom typography definition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


// -----------------------------------------------------------------
// LIGHT COLOR SCHEME
// Focused on vibrant, warm colors for the light mode background.
// -----------------------------------------------------------------
private val LightColors = lightColorScheme(

    // Custom Primary (Use the main purple from the login screen gradient)
    primary = Color(0xFF9333EA), // Bright Purple
    onPrimary = Color.White,

    // Secondary (Use the secondary color from the gradient or a complimentary one)
    secondary = Color(0xFFEC4899), // Pink
    onSecondary = Color.White,

    // Background uses a soft off-white from your Colors.kt if available, otherwise a default light.
    background = Color(0xFFFFFBFE), // Standard light background
    onBackground = Color(0xFF1C1B1F),

    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),

    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),

    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),

    inverseSurface = Color(0xFFF4EFF4),
    surfaceTint = Color(0xFF9333EA),

    // Reverting to default values for other containers for consistency
    surfaceContainer = Color(0xFFF3EDF7),
    surfaceContainerLow = Color(0xFFF7F2FA),
)


// -----------------------------------------------------------------
// DARK COLOR SCHEME
// Focused on deep, dark colors for the dark mode background.
// -----------------------------------------------------------------
private val DarkColors = darkColorScheme(

    // Custom Primary (Light purple for visibility on dark backgrounds)
    primary = Color(0xFFB388FF), // Light Purple from dark login screen focus
    onPrimary = Color(0xFF381E72),

    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),

    // Background uses a deep, dark gray/black from your Colors.kt (BackgroundDark or standard dark)
    background = Color(0xFF1C1B1F), // Standard dark background
    onBackground = Color(0xFFE6E1E5),

    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),

    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),

    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),

    inverseSurface = Color(0xFFE6E1E5),
    surfaceTint = Color(0xFFB388FF),

    surfaceContainer = Color(0xFF1C1B1F),
    surfaceContainerLow = Color(0xFF141218),
)


@Composable
fun SmartCourseTheme(content: @Composable () -> Unit) {

    val useDark = isSystemInDarkTheme()

    MaterialTheme(
        colorScheme = if (useDark) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}