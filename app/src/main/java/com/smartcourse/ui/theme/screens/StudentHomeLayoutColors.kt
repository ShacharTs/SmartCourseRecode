package com.smartcourse.ui.theme.screens

import androidx.compose.ui.graphics.Color

/**
 * Holds all screen-specific colors for the Student Home screen.
 * Aligned with Student Menu SVGs (Light / Dark).
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
 * These values now MATCH the Student Menu SVG designs.
 */
object StudentHomeLayoutColors {

    /**
     * DARK MODE — matches StudentMenuLayoutDarkMode.svg
     */
    val Dark = StudentHomeColorPalette(
        // Deep dark background (top of gradient)
        background = Color(0xFF0B0514),

        // Card surface used in SVG (#16112A / #171122 range)
        card = Color(0xFF16112A),

        // Accent purple used for avatars / highlights
        accent = Color(0xFF3A0F54),

        // Secondary text
        subtext = Color(0xFFA9A9B3),

        // Rating star
        star = Color(0xFFFFD54F),

        // Primary text
        textPrimary = Color.White
    )

    /**
     * LIGHT MODE — matches StudentMenuLayoutLightMode.svg
     */
    val Light = StudentHomeColorPalette(
        // Light gradient base (derived midpoint, NOT white)
        background = Color(0xFFF5EAFE),

        // White cards on gradient background
        card = Color.White,

        // Strong brand purple (SVG accent)
        accent = Color(0xFF9333EA),

        // Muted gray text
        subtext = Color(0xFF6B7280),

        // Orange rating star
        star = Color(0xFFF97316),

        // Near-black primary text
        textPrimary = Color(0xFF111827)
    )
}
