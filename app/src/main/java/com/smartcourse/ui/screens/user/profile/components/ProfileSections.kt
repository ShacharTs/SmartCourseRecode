package com.smartcourse.ui.screens.user.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.theme.ShowProfileColorPalette

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
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(home.card)
                .padding(top = 64.dp, bottom = 24.dp),
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
        Avatar(user = user, home = home, onEditAvatar = onEditAvatar)
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun BoxScope.Avatar(user: User, home: ShowProfileColorPalette, onEditAvatar: () -> Unit) {
    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .size(76.dp)
            .clip(CircleShape)
            .background(home.avatarBackground)
            .clickable { onEditAvatar() },
        contentAlignment = Alignment.Center
    ) {
        if (!user.image.isNullOrBlank()) {
            AsyncImage(
                model = user.image,
                contentDescription = "Profile image",
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = user.name?.split(" ")?.take(2)?.joinToString("") { it.first().uppercase() }.orEmpty(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
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