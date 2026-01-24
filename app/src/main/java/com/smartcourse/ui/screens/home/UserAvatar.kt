package com.smartcourse.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.theme.StudentHomeColorPalette


@Composable
fun UserAvatar(
    user: User,
    colors: StudentHomeColorPalette,

    // Configuration
    size: Dp = 48.dp,
    showName: Boolean = true,
    nameFontSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Medium,
    spacing: Dp = 8.dp,

    // Interaction
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick != null)
            Modifier.clickable { onClick() }
        else Modifier
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(user.image)
                .crossfade(true)
                .build(),
            contentDescription = "User avatar",
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(colors.accent)
        )

        if (showName) {
            Spacer(Modifier.height(spacing))
            Text(
                text = user.displayName,
                color = colors.textPrimary,
                fontSize = nameFontSize,
                fontWeight = fontWeight
            )
        }
    }
}


