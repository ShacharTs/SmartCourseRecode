package com.smartcourse.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.theme.StudentHomeColorPalette

/**
 * Reusable grid-based user section.
 * Mirrors HorizontalUserSection but uses a grid layout.
 */
@Composable
fun UserGridSection(
    title: String,
    users: List<User>,
    colors: StudentHomeColorPalette,
    emptyText: String,
    columns: Int = 2,
    maxItems: Int? = null,
    content: @Composable (User) -> Unit
) {
    Column {
        Text(
            text = title,
            color = colors.textPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        val shownUsers = maxItems?.let { users.take(it) } ?: users

        if (shownUsers.isEmpty()) {
            Text(emptyText, color = colors.subtext)
            return@Column
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            shownUsers
                .chunked(columns)
                .forEach { rowUsers ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowUsers.forEach { user ->
                            Box(modifier = Modifier.weight(1f)) {
                                content(user)
                            }
                        }

                        // Fill missing columns for last row
                        repeat(columns - rowUsers.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
        }
    }
}

