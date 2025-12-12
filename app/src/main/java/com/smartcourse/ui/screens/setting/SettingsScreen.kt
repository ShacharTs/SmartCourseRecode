package com.smartcourse.ui.screens.setting

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.ui.screens.components.CustomBox
import com.smartcourse.ui.screens.components.CustomText


@Composable
fun SettingsScreen(
    navController: NavController,
    authVM: AuthViewModel
){
    CustomBox(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ){
        CustomText(
            text = " Temp Settings Screen"
        )
    }

}