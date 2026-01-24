package com.smartcourse.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.screens.components.CustomButton
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
            .width(156.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colors.card)
            .clickable { onProfileClick() }
            .padding(12.dp)
    ) {
        Column(

            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                UserAvatar(
                    user = user,
                    colors = colors,
                    size = 32.dp,
                    showName = false
                )

                Spacer(Modifier.width(8.dp))

                Column {
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
                            fontSize = 11.sp
                        )
                    }
                }
            }


            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CustomButton(
                    text = "Chat",
                    modifier = Modifier.weight(1f),
                    backgroundColor = colors.accent,
                    contentColor = colors.textPrimary,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
                    onClick = onChatClick
                )

                CustomButton(
                    text = "Save",
                    modifier = Modifier.weight(1f),
                    backgroundColor = colors.card,
                    contentColor = colors.textPrimary,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
                    onClick = onSaveClick
                )
            }


        }
    }
}