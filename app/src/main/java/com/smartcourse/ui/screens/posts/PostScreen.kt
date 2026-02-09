package com.smartcourse.ui.screens.posts



import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartcourse.data.models.usermodel.User


@Composable
fun MyPostScreen(navController: NavController) {
    val userId = navController.currentBackStackEntry
        ?.arguments
        ?.getString("userId") ?: return
    val postViewModel: PostViewModel = hiltViewModel(key = "post-$userId")

    val user by postViewModel.currentUserFlow.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ){
        Test(user)
    }

}



@Composable
fun Test(user : User?){

    Text(
        text = "Your name is: ${user?.displayName ?: "Unknown"}",
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = Color.White
    )


}