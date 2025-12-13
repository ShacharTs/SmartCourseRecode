package com.smartcourse.ui.theme.screens

import androidx.compose.ui.graphics.Color

/**
 * Holds all screen-specific colors for the Bottom / Top Navigation bars.
 * Intentionally separated from MaterialTheme to keep navigation styling explicit.
 */
data class NavBarColorPalette(
    val background: Color,
    val iconSelected: Color,
    val iconUnselected: Color,
    val textSelected: Color,
    val textUnselected: Color,
    val divider: Color
)

/**
 * Screen-specific color palettes for Navigation Bars (Light / Dark).
 * These values are aligned with Chat + Profile themes.
 */
object NavBarColors {

    /** Dark Mode Navigation Bar */
    val Dark = NavBarColorPalette(
        // Same surface used in Chat input bar / bottom nav SVG
        background = Color(0xFF16112A),

        // Primary action color (same as chat "me" bubble)
        iconSelected = Color(0xFFEC4899),
        textSelected = Color(0xFFFFFFFF),

        // Muted inactive state
        iconUnselected = Color(0xFFA9A9B3),
        textUnselected = Color(0xFFA9A9B3),

        // Optional divider / top border
        divider = Color(0xFF241636)
    )

    /** Light Mode Navigation Bar */
    val Light = NavBarColorPalette(
        // Light surface, matches chat/profile light
        background = Color.White,

        // Primary brand color
        iconSelected = Color(0xFFEC4899),
        textSelected = Color(0xFFEC4899),

        // Muted gray for unselected
        iconUnselected = Color(0xFF6B7280),
        textUnselected = Color(0xFF6B7280),

        // Subtle divider on white
        divider = Color(0xFFE5E7EB)
    )
}


object NavBarGradients {

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
