package com.smartcourse.ui.screens.user

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.ui.screens.components.CustomColumn
import com.smartcourse.ui.screens.components.CustomSpacer
import com.smartcourse.ui.screens.components.CustomText


@Composable
fun UserHomeLayout(
    navController: NavController,
    authVM: AuthViewModel
) {
    val user = authVM.user

    CustomColumn(
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        CustomText(
            text = "User name: ${user?.getUserName()}"
        )
        CustomSpacer(height = 20)

        CustomText(
            text = "User role: ${user?.getUserRole()}"
        )
    }



}