package com.smartcourse.ui.screens.navbar

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.screens.components.CustomText
import com.smartcourse.ui.theme.screens.NavBarColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTopAppBar(
    navController: NavController,
    authVM: AuthViewModel
) {
    // Use the same NavBar color contract
    val colors = if (isSystemInDarkTheme()) {
        NavBarColors.Dark
    } else {
        NavBarColors.Light
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colors.background,
            navigationIconContentColor = colors.iconUnselected,
            actionIconContentColor = colors.iconUnselected,
            titleContentColor = colors.textSelected
        ),
        navigationIcon = {
            IconButton(onClick = {
                navController.navigate(Screen.Settings.route)
            }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        },
        title = {
            CustomText(
                modifier = Modifier.fillMaxWidth(),
                text = "Welcome ${authVM.currentUser.value?.getUserName()}",
                fontSize = 30.sp,
                maxLines = 1,
                textAlign = TextAlign.Center,
                color = colors.textSelected
            )
        },
        actions = {
            IconButton(onClick = {
                authVM.logout()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout"
                )
            }
        }
    )
}
