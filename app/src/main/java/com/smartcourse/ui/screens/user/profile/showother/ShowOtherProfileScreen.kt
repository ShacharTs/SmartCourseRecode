@file:Suppress("DEPRECATION")

package com.smartcourse.ui.screens.user.profile.showother

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
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
import com.smartcourse.data.models.usermodel.Course
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.screens.loading.LoadingScreen
import com.smartcourse.ui.theme.LocalAppPalette
import com.smartcourse.ui.theme.ShowOtherProfileColorPalette



//todo use the most method from Profile so avoid dupes method
@Composable
fun ShowOtherProfileScreen(
    navController: NavController
) {
    val backStackEntry = navController.currentBackStackEntry
        ?: return

    val userId = backStackEntry.arguments
        ?.getString("userId")
        ?: return

    // avoid incorrect profile show, use ID to split
    val showOtherProfileVM: ShowOtherProfileViewModel = hiltViewModel(key = "ShowOtherProfile-$userId")

    val palette = LocalAppPalette.current
    val colors = palette.otherProfile

    val isLoading by showOtherProfileVM.isLoading.collectAsStateWithLifecycle()
    val user by showOtherProfileVM.user.collectAsStateWithLifecycle()
    val favorites by showOtherProfileVM.favoritesCount.collectAsStateWithLifecycle()
    val courses by showOtherProfileVM.courses.collectAsStateWithLifecycle()
    val isFavorite by showOtherProfileVM.isFavorite.collectAsStateWithLifecycle()
    val isToggling by showOtherProfileVM.isTogglingFavorite.collectAsStateWithLifecycle()

    if (isLoading || user == null) {
        LoadingScreen()
        return
    }

    val currentUser = user!!

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
            userName = currentUser.displayName,
            role = currentUser.role?.name,
            image = currentUser.image,
            favorites = favorites,
            coursesCount = courses.size,
            isFavorite = isFavorite,
            isToggling = isToggling,
            onChatClick = {
                showOtherProfileVM.openChat { chatId ->
                    navController.navigate(Screen.ChatRoom.createRoute(chatId))
                }
            },
            onFavoriteClick = {
                showOtherProfileVM.toggleFavorite()
            }
        )

        Spacer(Modifier.height(32.dp))
        AboutSection(colors = colors, bio = currentUser.bio)

        if (courses.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            CoursesSection(colors, courses)
        }
    }
}





@Composable
private fun ProfileHeader(
    colors: ShowOtherProfileColorPalette,
    userName: String,
    role: String?,
    image: String?,
    favorites: Int,
    coursesCount: Int,
    isFavorite: Boolean,
    isToggling: Boolean,
    onChatClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(colors.card)
                .padding(top = 64.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserTitle(userName, role, colors)
            StatsRow(favorites, coursesCount, colors)
            ActionButtons(
                colors = colors,
                isFavorite = isFavorite,
                isToggling = isToggling,
                onChatClick = onChatClick,
                onFavoriteClick = onFavoriteClick
            )
        }
        Avatar(image, colors)
    }
}


@Composable
private fun UserTitle(name: String, role: String?, colors: ShowOtherProfileColorPalette) {
    Text(name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
    Spacer(Modifier.height(4.dp))
    Text(
        role?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "",
        fontSize = 14.sp,
        color = colors.subtext
    )
}

@Composable
private fun StatsRow(favorites: Int, courses: Int, colors: ShowOtherProfileColorPalette) {
    Spacer(Modifier.height(20.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        StatItem(favorites.toString(), "Favorites", colors)
        StatItem(courses.toString(), "Courses", colors)
    }
}

//todo remove and use CustomButton for that
@Composable
private fun ActionButtons(
    colors: ShowOtherProfileColorPalette,
    isFavorite: Boolean,
    isToggling: Boolean, // Added
    onChatClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Spacer(Modifier.height(20.dp))

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onFavoriteClick,
            enabled = !isToggling,
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                1.dp,
                // הלב יהיה אדום רק אם isFavorite הוא true
                if (isFavorite) Color.Red else colors.accent.copy(alpha = 0.55f)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = colors.card,

                contentColor = if (isFavorite) Color.Red else colors.accent
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(

                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) Color.Red else colors.accent,
                modifier = Modifier.size(22.dp)
            )
        }

        Button(
            onClick = onChatClick,
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
        ) {
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = "Chat",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Chat", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}



//todo remove and user UserAvatar later
@Composable
private fun Avatar(
    image: String?,
    colors: ShowOtherProfileColorPalette
) {
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(colors.card),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
        )
    }
}




@Composable
private fun AboutSection(
    colors: ShowOtherProfileColorPalette,
    bio: String?
) {
    Text(
        "About",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = colors.textPrimary,
        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
    )

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .padding(16.dp)
    ) {
        Text(
            bio ?: "No description available yet.",
            fontSize = 14.sp,
            color = colors.subtext,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun CoursesSection(
    colors: ShowOtherProfileColorPalette,
    courses: List<Course>
) {
    Text(
        "Courses",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = colors.textPrimary,
        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
    )

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .padding(16.dp)
    ) {
        courses.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { course ->
                    Text(
                        text = course.name,
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary
                    )
                }

                // fill empty cells
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}


@Composable
private fun StatItem(
    value: String,
    label: String,
    colors: ShowOtherProfileColorPalette
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            color = colors.subtext
        )
    }
}
