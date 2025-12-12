//package com.smartcourse.ui.screens.user.student
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.Logout
//import androidx.compose.material.icons.filled.Settings
//import androidx.compose.material3.CenterAlignedTopAppBar
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.smartcourse.auth.AuthViewModel
//import com.smartcourse.data.models.usermodel.User
//import com.smartcourse.ui.screens.components.CustomButton
//import com.smartcourse.ui.screens.components.CustomColumn
//import com.smartcourse.ui.screens.components.CustomDivider
//import com.smartcourse.ui.screens.components.CustomSpacer
//import com.smartcourse.ui.screens.components.CustomText
//import com.smartcourse.viewmodels.StudentViewModel
//
//
//
//// ------------------------------------------------------------
//// MAIN LAYOUT
//// ------------------------------------------------------------
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun StudentMenuLayout(
//    navController: NavController,
//    studentVM: StudentViewModel,
//    authVM: AuthViewModel
//) {
//    Scaffold(
//        topBar = {
//            StudentMenuTopBar(
//                navController = navController,
//                authVM = authVM
//            )
//        }
//    ) { padding ->
//        StudentMenuContent(
//            modifier = Modifier.padding(padding),
//            navController = navController,
//            studentVM = studentVM,
//            tutors = studentVM.matches.value
//        )
//    }
//}
//
//// ------------------------------------------------------------
//// TOP BAR
//// ------------------------------------------------------------
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun StudentMenuTopBar(
//    navController: NavController,
//    authVM: AuthViewModel
//) {
//    CenterAlignedTopAppBar(
//        title = {
//            Text("Welcome ${authVM.user?.getUserName() ?: ""}")
//        },
//        navigationIcon = {
//            IconButton(onClick = { /* Settings future */ }) {
//                Icon(Icons.Default.Settings, contentDescription = "Settings")
//            }
//        },
//        actions = {
//            IconButton(
//                onClick = {
//                    authVM.logout()
//                    navController.popBackStack(0, true)
//                }
//            ) {
//                Icon(
//                    Icons.AutoMirrored.Filled.Logout,
//                    contentDescription = "Logout"
//                )
//            }
//        }
//    )
//}
//
//// ------------------------------------------------------------
//// CONTENT AREA
//// ------------------------------------------------------------
//@Composable
//fun StudentMenuContent(
//    modifier: Modifier = Modifier,
//    navController: NavController,
//    studentVM: StudentViewModel,
//    tutors: List<User> = emptyList()
//) {
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        // --- TITLE ---
//        CustomSpacer(height = 50)
//
//        // --- TEMP BUTTONS ---
//        CustomButton(
//            text = "Find Tutor",
//            modifier = Modifier.fillMaxWidth(),
//            height = 100.dp,
//            onClick = {
//
//            }
//        )
//
//
//        CustomSpacer(100)
//
//        CustomButton(
//            text = "Create Post",
//            modifier = Modifier.fillMaxWidth(),
//            height = 100.dp,
//            onClick = {
//
//            }
//        )
//        CustomSpacer(height = 20)
//
//        CustomDivider()
//        CustomSpacer(height = 20)
//
//        // --- TUTORS ---
//        CustomText(
//            text = "Tutors",
//            fontSize = 32.sp,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        // Todo change to go profile, need to make a separate screen for show profile (not edit)
//        TutorGrid(tutors) { tutor ->
//            studentVM.openChatWith(tutor.getUID(), navController)
//        }
//
//    }
//}
//
//
//
//@Composable
//fun TutorGrid(tutors: List<User>, onTutorClick: (User) -> Unit) {
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        modifier = Modifier.fillMaxSize(),
//        contentPadding = PaddingValues(12.dp)
//    ) {
//        items(tutors) { tutor ->
//            showTutorCard(tutor) {
//                onTutorClick(tutor)
//            }
//        }
//    }
//
//}
//
//
//@Composable
//fun showTutorCard(
//    tutor: User,
//    onClick: () -> Unit = {}
//) {
//    CustomColumn(
//        modifier = Modifier
//            .padding(8.dp)
//            .clip(RoundedCornerShape(12.dp))
//            .clickable { onClick() }
//            .background(MaterialTheme.colorScheme.surfaceVariant)
//            .padding(12.dp)
//            .fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        CustomText(tutor.getUserName())
//
//
//        CustomSpacer(height = 8)
//
//        CustomText("Courses:",
//            style = MaterialTheme.typography.bodyMedium,
//            fontWeight = FontWeight.Bold,
//            color = MaterialTheme.colorScheme.onSurfaceVariant)
//
//        tutor.courses.forEach { course ->
//            Text("• ${course.name}")
//        }
//
//    }
//}
//
//
