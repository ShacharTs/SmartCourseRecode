package com.smartcourse.ui.screens.user.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.ui.theme.ShowProfileColorPalette

@Composable
fun CardSection(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF241636)) // Using your specific dark color
            .padding(16.dp),
        content = content
    )
}

@Composable
fun SectionHeader(title: String, onEditClick: () -> Unit, home: ShowProfileColorPalette) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = home.textPrimary)
        Text(
            text = "Edit",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = home.accent,
            modifier = Modifier.clickable { onEditClick() }
        )
    }
}

@Composable
fun ProfileActions(authVM: AuthViewModel) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFB71C1C))
            .clickable { authVM.logout() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Logout", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}