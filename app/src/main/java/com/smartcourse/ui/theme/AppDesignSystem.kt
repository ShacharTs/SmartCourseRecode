package com.smartcourse.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// DATA CLASSES
// ==========================================

data class LoginColorPalette(
    val fieldBorder: Color,
    val fieldFocused: Color,
    val placeholder: Color,
    val text: Color,
    val errorText: Color,
    val errorBackground: Color
)

data class RegisterColorPalette(
    val fieldBorder: Color,
    val fieldFocused: Color,
    val placeholder: Color,
    val text: Color,
    val errorText: Color,
    val buttonBackground: Color
)

data class SettingScreenColorPalette(
    val cardBackground: Color,
    val headerBackground: Color,
    val headerText: Color,
    val sectionTitle: Color,
    val rowText: Color,
    val secondaryText: Color,
    val dangerText: Color,
    val toggleTrackActive: Color,
    val toggleTrackInactive: Color,
    val toggleThumbActive: Color,
    val toggleThumbInactive: Color
)

data class StudentHomeColorPalette(
    val background: Color,
    val card: Color,
    val accent: Color,
    val subtext: Color,
    val star: Color,
    val textPrimary: Color
)

data class ChatListPalette(
    val background: Color,
    val card: Color,
    val accent: Color,
    val subtext: Color,
    val textPrimary: Color
)

data class ChatRoomPalette(
    val background: Color,
    val card: Color,
    val accent: Color,
    val subtext: Color,
    val textPrimary: Color,

    // 🔽 MESSAGE COLORS
    val incomingBubble: Color,
    val outgoingBubble: Color,
    val incomingBorder: Color,

    val backgroundGradient: List<Color>
)




data class NavBarColorPalette(
    val background: Color,
    val iconSelected: Color,
    val iconUnselected: Color,
    val textSelected: Color,
    val textUnselected: Color,
    val divider: Color,
    val title: Color
)

// ==========================================
// COLOR OBJECTS
// ==========================================

object SettingScreenColors {
    val Dark = SettingScreenColorPalette(
        cardBackground = Color(0xFF16112A),
        headerBackground = Color(0xFF1E0938),
        headerText = Color.White,
        sectionTitle = Color.White,
        rowText = Color(0xFFE5E7EB),
        secondaryText = Color(0xFFA9A9B3),
        dangerText = Color(0xFFFF4D4D),
        toggleTrackActive = Color(0xFF241636),
        toggleTrackInactive = Color(0xFF241636),
        toggleThumbActive = Color(0xFFEC4899),
        toggleThumbInactive = Color(0xFFA9A9B3)
    )

    val Light = SettingScreenColorPalette(
        cardBackground = Color.White,
        headerBackground = Color(0xFFF3E8FF),
        headerText = Color(0xFF2D1B4E),
        sectionTitle = Color(0xFF6B42C1),
        rowText = Color(0xFF374151),
        secondaryText = Color(0xFF9CA3AF),
        dangerText = Color(0xFFE11D48),
        toggleTrackActive = Color(0xFFD8B4FE),
        toggleTrackInactive = Color(0xFFE5E7EB),
        toggleThumbActive = Color(0xFF9333EA),
        toggleThumbInactive = Color.White
    )
}

object LoginScreenColors {
    val Dark = LoginColorPalette(
        fieldBorder = Color.White,
        fieldFocused = Color(0xFFB388FF),
        placeholder = Color(0xFFBBBBBB),
        text = Color.White,
        errorText = Color.Red,
        errorBackground = Color.Transparent
    )

    val Light = LoginColorPalette(
        fieldBorder = Color(0xFFD1D5DB),
        fieldFocused = Color(0xFF9333EA),
        placeholder = Color(0xFF000000),
        text = Color(0xFF000000),
        errorText = Color(0xFFE11D48),
        errorBackground = Color(0xFFFEE2E2)
    )
}

// Add to COLOR OBJECTS section:
object RegisterScreenColors {
    val Dark = RegisterColorPalette(
        fieldBorder = Color.White,
        fieldFocused = Color(0xFFB388FF),
        placeholder = Color(0xFFBBBBBB),
        text = Color.White,
        errorText = Color.Red,
        buttonBackground = Color(0xFFB388FF)
    )

    val Light = RegisterColorPalette(
        fieldBorder = Color(0xFFD1D5DB),
        fieldFocused = Color(0xFF9333EA),
        placeholder = Color(0xFF9CA3AF),
        text = Color(0xFF1F2937),
        errorText = Color(0xFFE11D48),
        buttonBackground = Color(0xFF9333EA)
    )
}

object StudentHomeLayoutColors {
    val Dark = StudentHomeColorPalette(
        background = Color(0xFF0B0514),
        card = Color(0xFF16112A),
        accent = Color(0xFF3A0F54),
        subtext = Color(0xFFA9A9B3),
        star = Color(0xFFFFD54F),
        textPrimary = Color.White
    )

    val Light = StudentHomeColorPalette(
        background = Color(0xFFF5F3FF),
        card = Color.White,
        accent = Color(0xFF8B5CF6),
        subtext = Color(0xFF6B7280),
        star = Color(0xFFF59E0B),
        textPrimary = Color(0xFF1F2937)
    )
}

object ChatListLayoutColors {

    val Dark = ChatListPalette(
        background = Color(0xFF0B0514),
        card = Color(0xFF16112A),
        accent = Color(0xFF3A0F54),
        subtext = Color(0xFFA9A9B3),
        textPrimary = Color.White
    )

    val Light = ChatListPalette(
        background = Color(0xFFF5F3FF),
        card = Color.White,
        accent = Color(0xFF8B5CF6),
        subtext = Color(0xFF6B7280),
        textPrimary = Color(0xFF1F2937)
    )
}


object ChatRoomLayoutColors {

    val Dark = ChatRoomPalette(
        background = Color(0xFF0B0514),
        card = Color(0xFF16112A),
        accent = Color(0xFF3A0F54),
        subtext = Color(0xFFA9A9B3),
        textPrimary = Color.White,

        incomingBubble = Color(0xFF241A38),
        outgoingBubble = Color(0xFF3A0F54),
        incomingBorder = Color.White.copy(alpha = 0.08f),

        backgroundGradient = listOf(
            Color(0xFF140724),
            Color(0xFF1C0934),
            Color(0xFF260B40),
            Color(0xFF300D4A),
        )
    )


    val Light = ChatRoomPalette(
        background = Color(0xFFF5F3FF),
        card = Color.White,
        accent = Color(0xFF8B5CF6),
        subtext = Color(0xFF6B7280),
        textPrimary = Color(0xFF000000),

        incomingBubble = Color(0xFFF3E8FF),
        outgoingBubble = Color(0xFF8B5CF6),
        incomingBorder = Color(0xFF000000).copy(alpha = 0.05f),

        backgroundGradient = listOf(
            Color(0xFFF5F3FF),
            Color(0xFFEDE9FE),
            Color(0xFFDDD6FE),
        )



    )

}






object NavBarColors {
    val Dark = NavBarColorPalette(
        background = Color(0xFF16112A),
        iconSelected = Color(0xFFF375B3),
        textSelected = Color(0xFFF375B3),
        iconUnselected = Color(0xFFA9A9B3),
        textUnselected = Color(0xFFA9A9B3),
        divider = Color(0xFF241636),
        title = Color.White
    )

    val Light = NavBarColorPalette(
        background = Color.White,
        iconSelected = Color(0xFF7C3AED),
        textSelected = Color(0xFF7C3AED),
        iconUnselected = Color(0xFF9CA3AF),
        textUnselected = Color(0xFF9CA3AF),
        divider = Color(0xFFF3E8FF),
        title = Color(0xFF111827)
    )
}

// ==========================================
// GRADIENTS
// ==========================================

object AppGradients {
    val Dark = listOf(Color(0xFF0B0514), Color(0xFF1E0938), Color(0xFF3A0F54))
    val Light = listOf(Color(0xFFF5F3FF), Color(0xFFEDE9FE), Color(0xFFDDD6FE))
}