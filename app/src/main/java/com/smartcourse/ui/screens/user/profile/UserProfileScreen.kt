package com.smartcourse.ui.screens.user.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
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

    // Collect states from ViewModels
    val authState by authVM.currentUser.collectAsState()
    val user by vm.user.collectAsState()

    val context = LocalContext.current
    val authUser = authState?.user

    // Trigger data load when the auth user ID is available
    LaunchedEffect(authUser?.userId) {
        authUser?.userId?.let { id ->
            vm.loadUser(id)
            vm.loadCourses(id)
        }
    }

    // Handle Image Selection
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Note: Ideally, move the byte reading to a background thread/ViewModel
            val bytes = context.contentResolver.openInputStream(it)?.use { stream ->
                stream.readBytes()
            }
            if (bytes != null && authUser != null) {
                vm.updateAvatarPng(authUser.userId, bytes)
            }
        }
    }

    var showEditName by remember { mutableStateOf(false) }
    var showEditBio by remember { mutableStateOf(false) }

    // Use a Box to layer the Loading UI over or instead of the content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors.backgroundGradient))
            .statusBarsPadding()
    ) {
        if (authUser == null || user == null) {
            // Show a loading indicator instead of returning null/empty
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            // Content is safe to display here
            val currentUser = user!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 24.dp)
            ) {
                ProfileHeader(colors)

                ProfileCard(
                    user = currentUser,
                    home = colors,
                    onEditAvatar = { pickImageLauncher.launch("image/*") },
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

            // Dialogs placed inside the 'else' to ensure currentUser is available
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
    }
}