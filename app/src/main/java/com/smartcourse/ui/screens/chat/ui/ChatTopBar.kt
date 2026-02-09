package com.smartcourse.ui.screens.chat.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.screens.components.CustomImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    navController: NavController,
    otherUser: User?,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        title = {
            // Standard Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Kept your local CustomImage as requested
                CustomImage(otherUser?.image, size = 40.dp)

                // Standard Spacer
                Spacer(modifier = Modifier.width(12.dp))

                // Standard Text
                Text(
                    text = otherUser?.name ?: "Loading…",
                    color = Color.White
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }
    )
}