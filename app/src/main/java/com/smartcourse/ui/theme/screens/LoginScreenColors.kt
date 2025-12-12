package com.smartcourse.ui.theme.screens

import androidx.compose.ui.graphics.Color

/**
 * Data class to hold all screen-specific colors for the Login screen.
 */
data class LoginColorPalette(
    val fieldBorder: Color,
    val fieldFocused: Color,
    val placeholder: Color,
    val text: Color,
    val errorText: Color,
    val errorBackground: Color
)

/**
 * Screen-specific color palettes for Light and Dark modes.
 * This ensures clean separation from the global MaterialTheme colors.
 */
object LoginScreenColors {
    // Colors for the Light Theme variant of the Login Screen
    val Light = LoginColorPalette(
        // Field colors derived from the original hardcoded values or theme constants
        fieldBorder = Color(0xFF020000), // Near Black
        fieldFocused = Color(0xFF9333E1), // Primary Purple (Focus)
        placeholder = Color(0xFF000000), // Pure Black for better contrast on light fields
        text = Color.Black,             // Entered text
        errorText = Color(0xFFFF4444),  // Darker Red for visibility against bright backgrounds
        errorBackground = Color.Black.copy(alpha = 0.5f) // Semi-transparent black for contrast box
    )

    // Colors for the Dark Theme variant of the Login Screen
    val Dark = LoginColorPalette(
        // Field colors derived from the original hardcoded values or theme constants
        fieldBorder = Color(0xFFFFFFFF), // Pure White
        fieldFocused = Color(0xFFB388FF), // Primary Light Purple (Focus)
        placeholder = Color(0xFFBBBBBB), // Light Gray
        text = Color.White,             // Entered text
        errorText = Color.Red,          // Standard bright Red on dark background
        errorBackground = Color.Transparent // No background needed in dark mode
    )
}

// Example definition for LoginGradients object:
object LoginGradients {
    val Dark = listOf(
        Color(0xFF0B0514),
        Color(0xFF1E0938),
        Color(0xFF3A0F54)
    )

    val Light = listOf(
        Color(0xFF9333EA),
        Color(0xFFEC4899),
        Color(0xFFF97316)
    )
}

