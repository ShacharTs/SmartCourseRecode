@file:Suppress("DEPRECATION")

package com.smartcourse.ui.screens.user.profile.showother

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.smartcourse.ui.theme.LocalAppPalette
import com.smartcourse.ui.theme.ShowOtherProfileColorPalette

@Composable
fun ShowOtherProfileScreen(
    navController: NavController,
    viewModel: ShowOtherProfileViewModel = hiltViewModel()
) {
    val palette = LocalAppPalette.current
    val colors = palette.otherProfile
    val user by viewModel.user.collectAsState(initial = null)
    val favorites by viewModel.favoritesCount.collectAsState(initial = 0)
    val courses by viewModel.courses.collectAsState(initial = emptyList())



    if (user == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors.backgroundGradient)
            )
            .statusBarsPadding()
            .padding(bottom = 24.dp)
    ) {

        /* ======================
           PROFILE CARD + AVATAR
           ====================== */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            // Profile card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(colors.card)
                    .padding(top = 64.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = user!!.getUserName(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = user!!.role?.name
                        ?.lowercase()
                        ?.replaceFirstChar { it.uppercase() }
                        ?: "",
                    fontSize = 14.sp,
                    color = colors.subtext
                )

                Spacer(Modifier.height(20.dp))

                // Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(favorites.toString(), "Favorites", colors)
                    StatItem(user!!.courses.size.toString(), "Courses", colors)
                }

                Spacer(Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedButton(
                        onClick = { /* toggle favorite */ },
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(0.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    colors.accent.copy(alpha = 0.5f),
                                    colors.accent
                                )
                            )
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = colors.accent
                        )
                    }

                    Button(
                        onClick = { /* open chat */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.accent
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Chat",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Avatar (cutting the card)
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(colors.card),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = user!!.image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        /* ======================
           ABOUT
           ====================== */
        Text(
            text = "About",
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
                text = user!!.bio ?: "No description available yet.",
                fontSize = 14.sp,
                color = colors.subtext,
                lineHeight = 20.sp
            )



            Spacer(Modifier.height(24.dp))

        }

        if (courses.isNotEmpty()) {

            Spacer(Modifier.height(24.dp))

            SectionTitle("Courses", colors)

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.card)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                userScrollEnabled = false
            ) {
                items(courses) { course ->
                    Text(
                        text = course.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary
                    )
                }
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
            fontSize = 18.sp,
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


@Composable
private fun SectionTitle(
    text: String,
    colors: ShowOtherProfileColorPalette
) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = colors.textPrimary,
        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
    )
}

