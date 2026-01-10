package com.smartcourse.ui.screens.user

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.ui.theme.LocalAppPalette


@Composable
fun UserProfileScreen(
    navController: NavController,
    authVM: AuthViewModel
) {

    val palette = LocalAppPalette.current

    Text(
        modifier = Modifier.statusBarsPadding(),
        text = "Profile Screen Content",
        color = palette.home.textPrimary
    )


}