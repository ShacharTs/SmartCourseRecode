package com.smartcourse.ui.screens.user.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.screens.home.UserAvatar
import com.smartcourse.ui.theme.ShowProfileColorPalette
import com.smartcourse.ui.theme.StudentHomeColorPalette

@Composable
fun ProfileHeader(home: ShowProfileColorPalette) {
    Text(
        text = "Profile",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = home.textPrimary,
        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 12.dp)
    )
}

@Composable
fun ProfileCard(
    user: User,
    home: ShowProfileColorPalette,
    onEditAvatar: () -> Unit,
    onEditName: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(home.card)
                .padding(top = 44.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = user.name.orEmpty(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = home.textPrimary,
                modifier = Modifier.clickable { onEditName() }
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = user.role?.name?.lowercase()?.replaceFirstChar { it.uppercase() }.orEmpty(),
                fontSize = 14.sp,
                color = home.subtext
            )
        }

        Box(modifier = Modifier.align(Alignment.TopCenter)) {
            UserAvatar(
                user = user,
                // Corrected mapping based on your error messages
                colors = StudentHomeColorPalette(
                    accent = home.avatarBackground,
                    textPrimary = home.textPrimary,
                    background = home.card,
                    card = home.card,
                    subtext = home.subtext, // Use subtext instead of textSecondary
                    star = Color(0xFFFFD700) // Pass a default gold color for the star
                ),
                size = 80.dp,
                showName = false,
                onClick = onEditAvatar
            )
        }
    }
    Spacer(Modifier.height(24.dp))
}


@Composable
fun BioSection(user: User, home: ShowProfileColorPalette, onEditBio: () -> Unit) {
    SectionHeader(title = "Bio", onEditClick = onEditBio, home = home)
    CardSection {
        Text(
            text = user.bio ?: "No bio available yet.",
            fontSize = 14.sp,
            color = home.subtext,
            lineHeight = 20.sp
        )
    }
    Spacer(Modifier.height(24.dp))
}