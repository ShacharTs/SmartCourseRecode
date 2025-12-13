package com.smartcourse.ui.theme.screens

import androidx.compose.ui.graphics.Color

/**
 * Holds all screen-specific colors for the Student Home screen.
 * Matches the architecture used in LoginScreenColors.
 */
data class StudentHomeColorPalette(
    val background: Color,
    val card: Color,
    val accent: Color,
    val subtext: Color,
    val star: Color,
    val textPrimary: Color
)

/**
 * Light / Dark palettes for Student Home.
 */
object StudentHomeLayoutColors {

    val Dark = StudentHomeColorPalette(
        background = Color(0xFF0A0812),
        card = Color(0xFF171122),
        accent = Color(0xFF3A0F54),
        subtext = Color(0xFFA9A9B3),
        star = Color(0xFFFFD54F),
        textPrimary = Color.White
    )

    val Light = StudentHomeColorPalette(

        // Soft lavender-gray background (NO white beam)
        background = Color(0xFFEDE9F6),

        // Cards slightly darker than background
        card = Color(0xFFE2DDF1),

        // Your purple, slightly softened for daylight
        accent = Color(0xFF9333EA),

        // Secondary text (calm, readable)
        subtext = Color(0xFF5E5873),

        // Your orange, perfect for ratings
        star = Color(0xFFF97316),

        // Primary text (not pure black)
        textPrimary = Color(0xFF1C1A22)
    )


}
