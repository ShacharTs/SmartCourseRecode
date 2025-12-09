package com.smartcourse

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.ui.screens.components.CustomButton
import com.smartcourse.ui.screens.components.CustomColumn
import com.smartcourse.ui.screens.components.CustomSpacer

@Composable
fun DummyReachedScreen() {
    val authViewModel = hiltViewModel<AuthViewModel>()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        CustomColumn(

        ) {
            Text(
                text = "REACHED THIS PAGE",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Text("User: ${authViewModel.user?.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White)


            CustomSpacer(height = 30)

            CustomButton(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(vertical = 8.dp),
                text = "Logout ",
                onClick = {
                    authViewModel.logout()
                }
            )
        }


    }
}
