package com.smartcourse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.ui.screens.components.CustomColumn
import com.smartcourse.ui.screens.components.CustomSpacer

@Composable
fun DummyReachedScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {

    //val user = authViewModel

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

//            Text("User: ${user?.getUserName()}",
//                style = MaterialTheme.typography.bodyMedium,
//                color = Color.White)




            CustomSpacer(height = 30)
        }


    }
}
