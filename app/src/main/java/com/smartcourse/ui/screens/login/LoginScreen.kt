package com.smartcourse.ui.screens.login

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel


@Composable
fun LoginScreen(navController: NavController) {
    val authVM: AuthViewModel = hiltViewModel()

    // Test
    println(authVM.debug())
}


