package com.smartcourse.ui.screens.user.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.screens.user.profile.UserProfileViewModel
import com.smartcourse.ui.theme.ShowProfileColorPalette

@Composable
fun CoursesSection(
    home: ShowProfileColorPalette,
    user: User,
    vm: UserProfileViewModel
) {
    val userCourses by vm.courses.collectAsState()
    val allCourses by vm.allCourses.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.loadCourses(user.userId)
        vm.loadAllCourses()
    }

    SectionHeader(title = "Courses", onEditClick = { showDialog = true }, home = home)

    CardSection {
        if (userCourses.isEmpty()) {
            Text("No courses yet.", color = home.subtext)
        } else {
            userCourses.forEach {
                Text(text = "• ${it.name}", color = home.textPrimary, modifier = Modifier.padding(bottom = 8.dp))
            }
        }
    }

    if (showDialog) {
        EditCoursesDialog(
            allCourses = allCourses,
            userCourses = userCourses,
            onDismiss = { showDialog = false },
            onAdd = { course -> vm.addCourse(user.userId, course.id) },
            onRemove = { course -> vm.removeCourse(user.userId, course.id) }
        )
    }
}