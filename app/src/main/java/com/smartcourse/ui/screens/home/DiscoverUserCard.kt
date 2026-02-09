package com.smartcourse.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.theme.StudentHomeColorPalette

@Composable
fun DiscoverUserCard(
    user: User,
    colors: StudentHomeColorPalette,
    onProfileClick: () -> Unit,
    onChatClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(160.dp) // Adjusted slightly for better spacing
            .clip(RoundedCornerShape(14.dp))
            .background(colors.card)
            .clickable { onProfileClick() }
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Info Section
            Row(verticalAlignment = Alignment.CenterVertically) {
                UserAvatar(
                    user = user,
                    colors = colors,
                    size = 32.dp,
                    showName = false
                )

                Spacer(Modifier.width(8.dp))

                Column {
                    // Method for course display to handle logic in one place
                    UserCourseList(user, colors)
                }
            }

            // Action Buttons Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CardActionButton(
                    text = "Chat",
                    containerColor = colors.accent,
                    contentColor = colors.textPrimary,
                    onClick = onChatClick,
                    modifier = Modifier.weight(1f)
                )

                CardActionButton(
                    text = "Save",
                    containerColor = colors.card, // Using card color for outline-style look
                    contentColor = colors.textPrimary,
                    onClick = onSaveClick,
                    modifier = Modifier.weight(1f),
                    hasBorder = true
                )
            }
        }
    }
}

@Composable
private fun UserCourseList(user: User, colors: StudentHomeColorPalette) {
    user.courses.take(2).forEach { course ->
        Text(
            text = course.name,
            color = colors.subtext,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    if (user.courses.size > 2) {
        Text(
            text = "+${user.courses.size - 2} more",
            color = colors.subtext,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CardActionButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasBorder: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(8.dp),
        border = if (hasBorder) androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)) else null,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}