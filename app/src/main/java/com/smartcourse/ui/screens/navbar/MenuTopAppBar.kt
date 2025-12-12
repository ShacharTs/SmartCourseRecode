package com.smartcourse.ui.screens.navbar

import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.repositories.AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTopAppBar(
    navController: NavController,
    authVM: AuthViewModel
) {
    TopAppBar(
        title = { Text("Menu") },
        actions = {
            // Settings Button (מופיע ראשון משמאל לימין)
            IconButton(onClick = {
                // TODO: Implement navigation to Settings screen
                // navController.navigate(Screen.Settings.route)
                println("Navigate to Settings")
            }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings" // English comment
                )
            }

            // Logout Button (מופיע שני)
            IconButton(onClick = {
                authVM.logout()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout" // English comment
                )
            }
        }
    )
}