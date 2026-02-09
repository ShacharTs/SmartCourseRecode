package com.smartcourse.ui.screens.navbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.theme.LocalAppPalette
import com.smartcourse.ui.theme.NavBarColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTopAppBar(
    navController: NavController,
    authVM: AuthViewModel
) {
    val currentUser by authVM.currentUser.collectAsStateWithLifecycle()

    val palette = LocalAppPalette.current
    val colors = if (palette.isDark) NavBarColors.Dark else NavBarColors.Light

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
            Text(
                text = "Welcome ${currentUser?.displayName.orEmpty()}",
                fontSize = 30.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    )
}