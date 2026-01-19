package com.smartcourse.ui.screens.user.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.screens.user.profile.UserProfileViewModel
import com.smartcourse.ui.theme.ShowProfileColorPalette

@Composable
fun CoursesSection(
    home: ShowProfileColorPalette,
    user: User, // This is already the enriched User from the parent
    vm: UserProfileViewModel,
    authVM: AuthViewModel // Added to sync changes globally
) {
    // 1. Unified Data: Pull courses directly from the flat user object
    val userCourses = user.courses
    val allCourses by vm.allCourses.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    // 2. Optimized Loading: loadCourses is no longer needed as loadUser handles it
    LaunchedEffect(Unit) {
        vm.loadAllCourses()
    }

    SectionHeader(title = "Courses", onEditClick = { showDialog = true }, home = home)

    CardSection {
        if (userCourses.isEmpty()) {
            Text("No courses yet.", color = home.subtext)
        } else {
            userCourses.forEach { course ->
                Text(
                    text = "• ${course.name}",
                    color = home.textPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }

    if (showDialog) {
        EditCoursesDialog(
            allCourses = allCourses,
            userCourses = userCourses,
            onDismiss = { showDialog = false },
            // 3. Global Sync: Pass authVM to ensure Header/Home update
            onAdd = { course ->
                vm.addCourse(user.userId, course.id, authVM)
            },
            onRemove = { course ->
                vm.removeCourse(user.userId, course.id, authVM)
            }
        )
    }
}