package com.smartcourse.ui.screens.user.student

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.screens.home.DiscoverUserCard
import com.smartcourse.ui.screens.home.HomeScaffold
import com.smartcourse.ui.screens.home.HorizontalUserSection
import com.smartcourse.ui.screens.home.LatestChatsSection
import com.smartcourse.ui.screens.home.UserAvatar
import com.smartcourse.ui.screens.home.UserGridSection
import com.smartcourse.ui.theme.LocalAppPalette

@Composable
fun StudentHomeLayout(
    navController: NavController,
) {
    val studentVM: StudentHomeViewModel = hiltViewModel()

    val myTutors by studentVM.myTutors
    val discoverTutors by studentVM.discoverTutors
    val chats by studentVM.latestChats

    val palette = LocalAppPalette.current
    val homeColors = palette.home

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                studentVM.reload()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    HomeScaffold {
        item {
            HorizontalUserSection(
                title = "My Tutors",
                users = myTutors,
                colors = homeColors,
                emptyText = "No tutors yet",
                onUserClick = { tutor ->
                    studentVM.openChatWithTutor(
                        tutorId = tutor.userId,
                        navController = navController
                    )
                },
                avatar = { tutor ->
                    UserAvatar(
                        user = tutor,
                        colors = homeColors,
                        size = 40.dp,
                        showName = true,
                        onClick = {
                            studentVM.openChatWithTutor(
                                tutorId = tutor.userId,
                                navController = navController
                            )
                        }
                    )
                }
            )
        }

        item {
            UserGridSection(
                title = "Discover Tutors",
                users = discoverTutors,
                colors = homeColors,
                emptyText = "No tutors found",
                columns = 2,
                maxItems = 4,
            ) { tutor ->
                DiscoverUserCard(
                    user = tutor,
                    colors = homeColors,
                    onProfileClick = {
                        navController.navigate(
                            Screen.ShowOtherProfile.createRoute(tutor.userId)
                        )
                    },
                    onChatClick = {
                        studentVM.openChatWithTutor(
                            tutorId = tutor.userId,
                            navController = navController
                        )
                    },
                    onSaveClick = {
                        studentVM.saveUser(tutor)
                    }
                )
            }
        }

        item {
            LatestChatsSection(
                chats = chats,
                colors = homeColors
            )
        }
    }
}
