package com.smartcourse.ui.screens.user.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.Course
import com.smartcourse.data.models.usermodel.User
import com.smartcourse.ui.theme.LocalAppPalette
import com.smartcourse.ui.theme.ShowProfileColorPalette
import com.smartcourse.ui.theme.ShowProfileLayoutColors
import com.smartcourse.ui.theme.StudentHomeColorPalette

@Composable
fun UserProfileScreen(
    navController: NavController,
    authVM: AuthViewModel,
    vm: UserProfileViewModel = hiltViewModel()
) {
    val isDark = LocalAppPalette.current.isDark
    val colors =
        if (isDark) ShowProfileLayoutColors.Dark
        else ShowProfileLayoutColors.Light

    val authUser = authVM.currentUser.value?.user ?: return
    val user by vm.user.collectAsState()

    LaunchedEffect(authUser.userId) {
        vm.loadUser(authUser.userId)
        vm.loadCourses(authUser.userId)
    }

    val currentUser = user ?: return

    var showEditName by remember { mutableStateOf(false) }
    var showEditBio by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors.backgroundGradient))
            .statusBarsPadding()
            .padding(bottom = 24.dp)
    ) {
        ProfileHeader(colors)

        ProfileCard(
            user = currentUser,
            home = colors,
            onEditAvatar = { },
            onEditName = { showEditName = true }
        )

        BioSection(
            user = currentUser,
            home = colors,
            onEditBio = { showEditBio = true }
        )

        CoursesSection(
            home = colors,
            user = currentUser,
            vm = vm
        )

        ProfileActions(authVM)
    }

    if (showEditName) {
        EditNameDialog(
            currentName = currentUser.name.orEmpty(),
            onDismiss = { showEditName = false },
            onSave = { newName ->
                vm.updateName(currentUser.userId, newName)
            }
        )
    }

    if (showEditBio) {
        EditBioDialog(
            currentBio = currentUser.bio,
            onDismiss = { showEditBio = false },
            onSave = { newBio ->
                vm.updateBio(currentUser.userId, newBio)
            }
        )
    }
}



@Composable
private fun EditNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Text(
                "Save",
                modifier = Modifier.clickable {
                    onSave(name.trim())
                    onDismiss()
                }
            )
        },
        dismissButton = {
            Text("Cancel", modifier = Modifier.clickable { onDismiss() })
        },
        title = { Text("Edit name") },
        text = {
            androidx.compose.material3.TextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true
            )
        }
    )
}

@Composable
private fun EditBioDialog(
    currentBio: String?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var bio by remember { mutableStateOf(currentBio.orEmpty()) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Text(
                "Save",
                modifier = Modifier.clickable {
                    onSave(bio.trim())
                    onDismiss()
                }
            )
        },
        dismissButton = {
            Text("Cancel", modifier = Modifier.clickable { onDismiss() })
        },
        title = { Text("Edit bio") },
        text = {
            androidx.compose.material3.TextField(
                value = bio,
                onValueChange = { bio = it },
                minLines = 3
            )
        }
    )
}

@Composable
private fun EditCoursesDialog(
    allCourses: List<Course>,
    userCourses: List<Course>,
    onDismiss: () -> Unit,
    onAdd: (Course) -> Unit,
    onRemove: (Course) -> Unit
) {
    val userCourseIds = remember(userCourses) {
        userCourses.map { it.id }.toSet()
    }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Text("Done", modifier = Modifier.clickable { onDismiss() })
        },
        title = { Text("Edit Courses") },
        text = {
            Column {
                allCourses.forEach { course ->
                    val isAdded = userCourseIds.contains(course.id)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(course.name)

                        Text(
                            text = if (isAdded) "Remove" else "Add",
                            color = if (isAdded) Color.Red else Color(0xFF4CAF50),
                            modifier = Modifier.clickable {
                                if (isAdded) onRemove(course)
                                else onAdd(course)
                            }
                        )
                    }
                }
            }
        }
    )
}





@Composable
private fun ProfileHeader(home: ShowProfileColorPalette) {
    Text(
        text = "Profile",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = home.textPrimary,
        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 12.dp)
    )
}

@Composable
private fun ProfileCard(
    user: User,
    home: ShowProfileColorPalette,
    onEditAvatar: () -> Unit,
    onEditName: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(home.card)
                .padding(top = 64.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = user.name.orEmpty(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = home.textPrimary,
                modifier = Modifier.clickable { onEditName() }
            )


            Spacer(Modifier.height(4.dp))

            Text(
                text = user.role?.name
                    ?.lowercase()
                    ?.replaceFirstChar { it.uppercase() }
                    .orEmpty(),
                fontSize = 14.sp,
                color = home.subtext
            )
        }

        Avatar(
            user = user,
            home = home,
            onEditAvatar = onEditAvatar
        )
    }

    Spacer(Modifier.height(24.dp))
}

@Composable
private fun BoxScope.Avatar(
    user: User,
    home: ShowProfileColorPalette,
    onEditAvatar: () -> Unit
) {
    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .size(76.dp)
            .clip(CircleShape)
            .background(home.avatarBackground)
            .clickable { onEditAvatar() },
        contentAlignment = Alignment.Center
    ) {

        if (!user.image.isNullOrBlank()) {
            AsyncImage(
                model = user.image,
                contentDescription = "Profile image",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = user.name
                    ?.split(" ")
                    ?.take(2)
                    ?.joinToString("") { it.first().uppercase() }
                    .orEmpty(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}



@Composable
private fun BioSection(
    user: User,
    home: ShowProfileColorPalette,
    onEditBio: () -> Unit
) {
    SectionHeader(
        title = "Bio",
        onEditClick = onEditBio,
        home = home
    )

    CardSection {
        Text(
            text = user.bio ?: "No bio available yet.",
            fontSize = 14.sp,
            color = home.subtext,
            lineHeight = 20.sp
        )
    }

    Spacer(Modifier.height(24.dp))
}


@Composable
private fun CoursesSection(
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

    SectionHeader(
        title = "Courses",
        onEditClick = { showDialog = true },
        home = home
    )

    CardSection {
        if (userCourses.isEmpty()) {
            Text("No courses yet.", color = home.subtext)
        } else {
            userCourses.forEach {
                Text(
                    text = "• ${it.name}",
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
            onAdd = { course ->
                vm.addCourse(user.userId, course.id)
            },
            onRemove = { course ->
                vm.removeCourse(user.userId, course.id)
            }
        )
    }

    Spacer(Modifier.height(24.dp))
}



@Composable
private fun ProfileActions(authVM: AuthViewModel) {
    Spacer(Modifier.height(12.dp))

    PrimaryButton(
        text = "Logout",
        color = Color(0xFFB71C1C)
    ) {
        authVM.logout()
    }
}


@Composable
private fun CardSection(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF241636))
            .padding(16.dp),
        content = content
    )
}


@Composable
private fun SectionTitle(
    text: String,
    home: ShowProfileColorPalette
) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = home.textPrimary,
        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
    )
}


@Composable
private fun PrimaryButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}


@Composable
private fun SectionHeader(
    title: String,
    onEditClick: () -> Unit,
    home: ShowProfileColorPalette
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = home.textPrimary
        )

        Text(
            text = "Edit",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = home.accent,
            modifier = Modifier.clickable { onEditClick() }
        )
    }
}


@Composable
private fun ProfileRow(
    label: String,
    value: String,
    palette: StudentHomeColorPalette
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = palette.subtext
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = palette.textPrimary
        )
    }
}

