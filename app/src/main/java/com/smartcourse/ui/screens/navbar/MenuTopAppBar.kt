package com.smartcourse.ui.screens.navbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.screens.components.CustomText
import com.smartcourse.ui.theme.LocalAppPalette
import com.smartcourse.ui.theme.NavBarColors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTopAppBar(
    navController: NavController,
    authVM: AuthViewModel
) {
    val palette = LocalAppPalette.current
    val colors = if (palette.isDark) {
        NavBarColors.Dark
    } else {
        NavBarColors.Light
    }
    val currentUser = authVM.currentUser

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colors.background,
            navigationIconContentColor = colors.title,
            actionIconContentColor = colors.title,
            titleContentColor = colors.title
        ),
        navigationIcon = {
            IconButton(
                onClick = { navController.navigate(Screen.Settings.route) }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        },
        title = {
            CustomText(
                text = "Welcome ${currentUser.value?.displayName.orEmpty()}",
                fontSize = 30.sp,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    )
}


