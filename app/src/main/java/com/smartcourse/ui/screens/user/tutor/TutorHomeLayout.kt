package com.smartcourse.ui.screens.user.tutor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.smartcourse.ui.screens.home.HorizontalUserSection
import com.smartcourse.ui.screens.home.LatestChatsSection
import com.smartcourse.ui.screens.home.UserAvatar
import com.smartcourse.ui.theme.LocalAppPalette

@Composable
fun TutorHomeLayout(
    navController: NavController
) {
    val tutorVM: TutorHomeViewModel = hiltViewModel()
    val myStudents by tutorVM.myStudents
    val chats by tutorVM.latestChats

    val palette = LocalAppPalette.current
    val homeColors = palette.home

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                tutorVM.reload()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            HorizontalUserSection(
                title = "My Students",
                users = myStudents,
                colors = homeColors,
                emptyText = "No students yet",
                onUserClick = { student ->
                    tutorVM.openChatWithStudent(student.userId, navController)
                },
                avatar = { student ->
                    UserAvatar(
                        user = student,
                        colors = homeColors
                    )
                }
            )
        }

        item {
            LatestChatsSection(
                chats = chats,
                colors = homeColors
            )
        }
    }
}
