package com.smartcourse.ui.screens.user

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel


@Composable
fun UserProfileScreen(
    navController: NavController,
    authVM: AuthViewModel
) {

    Text(
        text = "Profile Screen Content",
        Modifier.statusBarsPadding()
    )
}