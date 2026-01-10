package com.smartcourse.ui.screens.user.profile.showother

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.smartcourse.ui.theme.LocalAppPalette
import com.smartcourse.ui.theme.ShowOtherProfileColorPalette

@Composable
fun ShowOtherProfileScreen(
    navController: NavController,
    viewModel: ShowOtherProfileViewModel = hiltViewModel()
) {
    val colors = LocalAppPalette.current.otherProfile
    val user by viewModel.user.collectAsState(initial = null)
    val favorites by viewModel.favoritesCount.collectAsState(initial = 0)

    if (user == null) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors.backgroundGradient))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            // Profile Image
            AsyncImage(
                model = user!!.image,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(colors.avatarBackground)
            )

            Spacer(Modifier.height(16.dp))

            // Name & Role
            Text(
                text = user!!.getUserName(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = user!!.role?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "",
                fontSize = 14.sp,
                color = colors.subtext
            )

            Spacer(Modifier.height(24.dp))

            // Stats Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.card)
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(favorites.toString(), "Favorites", colors)
                StatItem(user!!.courses.size.toString(), "Courses", colors)
            }

            Spacer(Modifier.height(24.dp))

            // Action Buttons Row (Favorite & Chat)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Favorite Button
                OutlinedButton(
                    onClick = { /* Toggle Favorite */ },
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(0.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(colors.accent.copy(alpha = 0.5f))
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color.White
                    )
                }

                // Chat Button
                Button(
                    onClick = { /* Open Chat */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Chat",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // About Section (Clean SVG Style)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "About",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = user!!.bio ?: "No description available yet.",
                    fontSize = 14.sp,
                    color = colors.subtext,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    colors: ShowOtherProfileColorPalette
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = colors.subtext
        )
    }
}