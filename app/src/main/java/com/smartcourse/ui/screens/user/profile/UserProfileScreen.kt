package com.smartcourse.ui.screens.user.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.ui.screens.user.profile.components.BioSection
import com.smartcourse.ui.screens.user.profile.components.CoursesSection
import com.smartcourse.ui.screens.user.profile.components.EditBioDialog
import com.smartcourse.ui.screens.user.profile.components.EditNameDialog
import com.smartcourse.ui.screens.user.profile.components.ProfileActions
import com.smartcourse.ui.screens.user.profile.components.ProfileCard
import com.smartcourse.ui.screens.user.profile.components.ProfileHeader
import com.smartcourse.ui.theme.LocalAppPalette
import com.smartcourse.ui.theme.ShowProfileLayoutColors

@Composable
fun UserProfileScreen(
    navController: NavController,
    authVM: AuthViewModel,
    vm: UserProfileViewModel = hiltViewModel()
) {
    val isDark = LocalAppPalette.current.isDark
    val colors = if (isDark) ShowProfileLayoutColors.Dark else ShowProfileLayoutColors.Light

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
            onEditAvatar = { /* Handle avatar edit */ },
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

        Spacer(Modifier.weight(1f))

        ProfileActions(authVM)
    }

    if (showEditName) {
        EditNameDialog(
            currentName = currentUser.name.orEmpty(),
            onDismiss = { showEditName = false },
            onSave = { newName -> vm.updateName(currentUser.userId, newName) }
        )
    }

    if (showEditBio) {
        EditBioDialog(
            currentBio = currentUser.bio,
            onDismiss = { showEditBio = false },
            onSave = { newBio -> vm.updateBio(currentUser.userId, newBio) }
        )
    }
}