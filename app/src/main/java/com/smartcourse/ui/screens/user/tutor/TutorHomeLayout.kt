package com.smartcourse.ui.screens.user.tutor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.screens.user.student.LatestChatsSection
import com.smartcourse.ui.theme.LocalAppPalette
import com.smartcourse.ui.theme.StudentHomeColorPalette

@Composable
fun TutorHomeLayout(
    navController: NavController,
    user: User,
) {
    val vm: TutorHomeViewModel = hiltViewModel()
    val myStudents by vm.myStudents
    val chats by vm.latestChats

    val palette = LocalAppPalette.current
    val homeColors = palette.home

    LaunchedEffect(user.userId) {
        vm.load(user)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            MyStudentsSection(
                students = myStudents,
                colors = homeColors,
                onStudentClick = { student ->
                    vm.openChatWithStudent(student.userId, navController)
                }
            )
        }

        item {
            LatestChatsSection(chats = chats, colors = homeColors)
        }
    }
}

    @Composable
    fun MyStudentsSection(
        //students: List<Student>,
        students: List<User>,
        colors: StudentHomeColorPalette,
        //onStudentClick: (Student) -> Unit
        onStudentClick: (User) -> Unit
    ) {
        Column {
            Text(
                "My Students",
                color = colors.textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (students.isEmpty()) {
                Text("No students assigned yet.", color = colors.subtext)
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    items(students) { student ->
                        StudentAvatar(student, colors) { onStudentClick(student) }
                    }
                }
            }
        }
    }

@Composable
fun StudentAvatar(
    student: User, // Changed from Student to User
    colors: StudentHomeColorPalette,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(student.image) // Direct access: student.image
                .crossfade(true)
                .build(),
            contentDescription = "Student avatar",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(colors.accent)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            student.displayName, // Direct access using your helper property
            color = colors.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}