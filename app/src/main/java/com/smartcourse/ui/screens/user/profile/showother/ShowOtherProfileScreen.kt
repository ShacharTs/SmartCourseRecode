package com.smartcourse.ui.screens.user.profile.showother

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.screens.loading.LoadingScreen
import com.smartcourse.ui.theme.LocalAppPalette
import com.smartcourse.ui.theme.ShowOtherProfileColorPalette


@Composable
fun ShowOtherProfileScreen(navController: NavController) {
    val userId = navController.currentBackStackEntry?.arguments?.getString("userId") ?: return
    val vm: ShowOtherProfileViewModel = hiltViewModel(key = "ShowOtherProfile-$userId")

    val palette = LocalAppPalette.current
    val colors = palette.otherProfile

    val isLoading by vm.isLoading.collectAsStateWithLifecycle()
    val user by vm.user.collectAsStateWithLifecycle()
    val favorites by vm.favoritesCount.collectAsStateWithLifecycle()
    val courses by vm.courses.collectAsStateWithLifecycle()
    val isFavorite by vm.isFavorite.collectAsStateWithLifecycle()
    val isToggling by vm.isTogglingFavorite.collectAsStateWithLifecycle()

    if (isLoading || user == null) { LoadingScreen(); return }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors.backgroundGradient))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        ProfileHeader(
            colors = colors,
            name = user!!.displayName,
            role = user!!.role?.name,
            img = user!!.image,
            favs = favorites,
            count = courses.size,
            isFav = isFavorite,
            isTog = isToggling,
            onChatClick = { vm.openChat { navController.navigate(Screen.ChatRoom.createRoute(it)) } },
            onFavoriteClick = { vm.toggleFavorite() }
        )

        ProfileSection(colors, "About", user!!.bio ?: "No description available yet.")

        if (courses.isNotEmpty()) {
            ProfileSection(colors, "Courses") {
                courses.chunked(3).forEach { row ->
                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                        row.forEach { Text(it.name, Modifier.weight(1f), fontSize = 14.sp, color = colors.textPrimary) }
                        repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    colors: ShowOtherProfileColorPalette,
    name: String, role: String?, img: String?, favs: Int, count: Int,
    isFav: Boolean, isTog: Boolean, onChatClick: () -> Unit, onFavoriteClick: () -> Unit
) {
    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(colors.card)
                .padding(top = 64.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
            Text(role?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "", fontSize = 14.sp, color = colors.subtext)

            Row(Modifier.fillMaxWidth().padding(top = 20.dp), Arrangement.SpaceEvenly) {
                StatItem(favs.toString(), "Favorites", colors)
                StatItem(count.toString(), "Courses", colors)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onFavoriteClick,
                    enabled = !isTog,
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (isFav) Color.Red else colors.accent.copy(0.55f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = if (isFav) Color.Red else colors.accent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, null, Modifier.size(22.dp))
                }

                Button(
                    onClick = onChatClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Icon(Icons.Outlined.ChatBubbleOutline, null, Modifier.size(20.dp), tint = Color.White)
                    Text("Chat", Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Corrected Avatar logic
        AsyncImage(
            model = img,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(76.dp)
                .border(4.dp, colors.card, CircleShape)
                .clip(CircleShape)
                .background(colors.card)
        )
    }
}

@Composable
private fun ProfileSection(
    colors: ShowOtherProfileColorPalette,
    title: String,
    content: String? = null,
    customContent: @Composable () -> Unit = {}
) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = colors.textPrimary,
        modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 16.dp, bottom = 8.dp)
    )
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .padding(16.dp)
    ) {
        if (content != null) {
            Text(content, fontSize = 14.sp, color = colors.subtext, lineHeight = 20.sp)
        } else {
            customContent()
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, colors: ShowOtherProfileColorPalette) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
        Text(label, fontSize = 12.sp, color = colors.subtext)
    }
}