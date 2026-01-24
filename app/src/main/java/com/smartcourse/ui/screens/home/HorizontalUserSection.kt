package com.smartcourse.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.theme.StudentHomeColorPalette


@Composable
fun HorizontalUserSection(
    title: String,
    users: List<User>,
    colors: StudentHomeColorPalette,
    emptyText: String,
    onUserClick: (User) -> Unit,
    avatar: @Composable (User) -> Unit
) {
    Column {
        Text(
            title,
            color = colors.textPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        if (users.isEmpty()) {
            Text(emptyText, color = colors.subtext)
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                items(users) { user ->
                    Box(modifier = Modifier.clickable { onUserClick(user) }) {
                        avatar(user)
                    }
                }
            }
        }
    }
}
