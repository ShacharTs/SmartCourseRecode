@file:Suppress("UNCHECKED_CAST")

package com.smartcourse.ui.screens.user

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.chat.ChatItem
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.ui.screens.components.CustomBox
import com.smartcourse.ui.screens.components.CustomColumn
import com.smartcourse.ui.screens.components.CustomImage
import com.smartcourse.ui.screens.components.CustomRow
import com.smartcourse.ui.screens.components.CustomSpacer
import com.smartcourse.ui.screens.components.CustomText
import com.smartcourse.viewmodels.TutorViewModel
import com.smartcourse.viewmodels.StudentViewModel

@Composable
fun UserMenuScreen(
    navController: NavController,
    authVM: AuthViewModel,
    role: UserRole,
    studentVM: StudentViewModel? = null,
    tutorVM: TutorViewModel? = null
) {

    Log.d("UserMenuScreen", "UserMenuScreen: ${authVM.user?.getUserName()}")
    Scaffold(
        topBar = {
            UserMenuTopBar(
                name = authVM.user?.getUserName() ?: "",
                navController = navController,
                authVM = authVM
            )
        }) { padding ->

        when (role) {

            UserRole.STUDENT -> {

                UserShow(
                    modifier = Modifier.padding(padding),
                    topTitle = "Recommended Tutors",
                    bottomTitle = "Latest Chats",
                    topItems = studentVM!!.recommendedTutors.value,
                    bottomItems = studentVM.recentChats.value,
                    onTopItemClick = { tutor ->
                        studentVM.openChatWith(tutor.getUID(), navController)
                    },
                    onBottomItemClick = { chat ->  }
                )


            }

            UserRole.TUTOR -> {
                UserShow(
                    modifier = Modifier.padding(padding),
                    topTitle = "My Students",
                    bottomTitle = "Latest Chats",
                    topItems = tutorVM!!.students.value,
                    bottomItems = tutorVM.recentChats.value,
                    onTopItemClick = { student ->
                        tutorVM.openChatWith(student.getUID(), navController)
                    },
                    onBottomItemClick = { chat ->  }
                )

            }

            UserRole.ADMIN -> {
                // todo make a screen for admin
            }


            else -> {
                //TEMP future
                // todo make a error 404 screen
                CustomText("Role not supported yet")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMenuTopBar(
    name: String, navController: NavController, authVM: AuthViewModel
) {
    CenterAlignedTopAppBar(title = {
        CustomText(
            text = "Welcome $name",
            fontSize = 25.sp,
        )
    }, navigationIcon = {
        IconButton(
            onClick = {
            //todo make a settings screen
        }) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }
    }, actions = {
        IconButton(
            onClick = {
                authVM.logout()
                navController.popBackStack(0, true)
            }) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
        }
    })
}



@Composable
fun UserShow(
    modifier: Modifier = Modifier,
    topTitle: String,
    bottomTitle: String,
    topItems: List<User>,
    bottomItems: List<ChatItem>,
    onTopItemClick: (User) -> Unit,
    onBottomItemClick: (ChatItem) -> Unit
) {

    CustomColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // --- TITLE ---
        CustomText(
            text = topTitle,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        // --- FIRST GRID (users) ---
        CustomBox(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .border(2.dp, MaterialTheme.colorScheme.primary)
                .padding(8.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize()
            ) {
                items(topItems) { user ->
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { onTopItemClick(user) }
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CustomText(user.getUserName())
                        CustomSpacer(8)
                        CustomText(
                            text = "More info…",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // --- SECOND GRID (latest chats) ---
        CustomBox(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .border(2.dp, MaterialTheme.colorScheme.primary)
                .padding(8.dp)
        ) {

            CustomColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                CustomText(
                    text = bottomTitle,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(bottomItems) { chat ->
                        CustomRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                                .clickable { onBottomItemClick(chat) }
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            CustomImage(
                                imageUrl = chat.otherUser?.image,
                                size = 50.dp,
                                shape = RoundedCornerShape(8.dp)
                            )

                            CustomSpacer(width = 12)

                            CustomColumn {
                                chat.otherUser?.name?.let { CustomText(it, fontWeight = FontWeight.Bold) }
                                CustomText(chat.lastMessage)
                            }
                        }
                    }
                }
            }
        }
    }
}
