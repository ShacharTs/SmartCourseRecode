package com.smartcourse.ui.screens.user.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
import com.smartcourse.ui.screens.chat.vm.GalleryEvent
import com.smartcourse.ui.screens.chat.vm.GalleryViewModel
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
    authVM: AuthViewModel = hiltViewModel(),
    userProfileViewModel: UserProfileViewModel = hiltViewModel(),
    galleryViewModel: GalleryViewModel = hiltViewModel()
) {
    val isDark = LocalAppPalette.current.isDark
    val colors = if (isDark) ShowProfileLayoutColors.Dark else ShowProfileLayoutColors.Light

    // 1. Observe the unified states
    val user by userProfileViewModel.user.collectAsState()
    val isUpdating by userProfileViewModel.isUpdating.collectAsState()
    val authUser = authVM.currentUserProfile
    val context = LocalContext.current

    // 2. Optimized Data Loading: loadUser now handles courses automatically
    LaunchedEffect(authUser?.userId) {
        authUser?.userId?.let { id ->
            userProfileViewModel.loadUser(id)
        }
    }

    // 3. Image Selection with Auth Sync
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.use { stream ->
                stream.readBytes()
            }
            if (bytes != null && authUser != null) {
                // Pass authVM to ensure the Header updates immediately
                userProfileViewModel.updateAvatarPng(authUser.userId, bytes, authVM)
            }
        }
    }

    LaunchedEffect(Unit) {
        galleryViewModel.events.collect { event ->
            if (event is GalleryEvent.OpenGallery) {
                pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }

    var showEditName by remember { mutableStateOf(false) }
    var showEditBio by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors.backgroundGradient))
            .statusBarsPadding()
    ) {
        // 4. Handle initial load vs. active updating
        if (authUser == null || user == null) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            // Use the local 'user' state as it is enriched with courses
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
                    onEditAvatar = { galleryViewModel.requestGallery() },
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
                    vm = userProfileViewModel,
                    authVM = authVM
                )

                Spacer(Modifier.weight(1f))

                ProfileActions(authVM)
            }

            // 5. Global Sync in Dialogs: Pass authVM to all updates
            if (showEditName) {
                EditNameDialog(
                    currentName = currentUser.name.orEmpty(),
                    onDismiss = { showEditName = false },
                    onSave = { newName ->
                        userProfileViewModel.updateName(currentUser.userId, newName, authVM)
                        showEditName = false
                    }
                )
            }

            if (showEditBio) {
                EditBioDialog(
                    currentBio = currentUser.bio,
                    onDismiss = { showEditBio = false },
                    onSave = { newBio ->
                        userProfileViewModel.updateBio(currentUser.userId, newBio, authVM)
                        showEditBio = false
                    }
                )
            }
        }

        // 6. Visual feedback during updates
        if (isUpdating) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}