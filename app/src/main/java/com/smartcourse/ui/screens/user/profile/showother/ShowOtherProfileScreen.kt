package com.smartcourse.ui.screens.user.profile.showother

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.smartcourse.ui.theme.LocalAppPalette


@Composable
fun ShowOtherProfileScreen(
    navController: NavController,
    viewModel: ShowOtherProfileViewModel = hiltViewModel()
) {
    val palette = LocalAppPalette.current
    val colors = palette.home
    val user by viewModel.user.collectAsState(initial = null)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        if (user == null) {
            Text(
                text = "Loading...",
                color = colors.subtext,
                fontSize = 16.sp
            )
            return@Box
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            // ===== Avatar =====
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.card),
                contentAlignment = Alignment.Center
            ) {
                if (!user!!.image.isNullOrBlank()) {
                    AsyncImage(
                        model = user!!.image,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = user!!.getUserName()
                            .split(" ")
                            .take(2)
                            .joinToString("") { it.first().uppercase() },
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ===== Name =====
            Text(
                text = user!!.getUserName(),
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )

            Spacer(Modifier.height(4.dp))

            // ===== Role =====
            user!!.role?.name?.lowercase()?.replaceFirstChar { it.uppercase() }?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = colors.subtext
                )
            }

            Spacer(Modifier.height(12.dp))

            // ===== Email =====
            Text(
                text = user!!.getUserEmail(),
                fontSize = 14.sp,
                color = colors.subtext
            )
        }
    }
}


