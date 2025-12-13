package com.smartcourse.ui.screens.user.tutor

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.Tutor


@Composable
fun TutorHomeLayout(
    navController: NavController,
    tutor: Tutor,
) {
    val tutorHomeViewModel: TutorHomeViewModel = hiltViewModel()

    LaunchedEffect(tutor.user.getUID()) {
        tutorHomeViewModel.load(tutor)
    }

    // TEMP UI (same level as Student)
    Text("Tutor: ${tutor.user.getUserName()}")
}
