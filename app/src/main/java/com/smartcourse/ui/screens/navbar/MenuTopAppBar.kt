package com.smartcourse.ui.screens.navbar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.screens.components.CustomText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTopAppBar(
    navController: NavController,
    authVM: AuthViewModel
) {
    TopAppBar(
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
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Welcome ${authVM.currentUser.value?.getUserName()}",
                fontSize = 30.sp,
                maxLines = 1,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        },
        actions = {
            // Logout Button
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