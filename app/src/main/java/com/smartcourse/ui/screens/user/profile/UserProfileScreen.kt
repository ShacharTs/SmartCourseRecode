package com.smartcourse.ui.screens.user.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.data.models.usermodel.User
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
import com.smartcourse.ui.theme.ShowProfileColorPalette
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

    val user by userProfileViewModel.user.collectAsState()
    val isUpdating by userProfileViewModel.isUpdating.collectAsState()
    val authUser = authVM.currentUserProfile

    var showEditName by remember { mutableStateOf(false) }
    var showEditBio by remember { mutableStateOf(false) }


    ProfileEffectHandlers(
        authVM = authVM,
        userProfileViewModel = userProfileViewModel,
        galleryViewModel = galleryViewModel
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .background(Brush.verticalGradient(colors.backgroundGradient))
            .statusBarsPadding()

    ) {
        if (authUser == null || user == null) {
            LoadingIndicator(Modifier.align(Alignment.Center))
        } else {
            ProfileContent(
                user = user!!,
                colors = colors,
                authVM = authVM,
                userProfileViewModel = userProfileViewModel,
                onEditAvatar = { galleryViewModel.requestGallery() },
                onEditName = { showEditName = true },
                onEditBio = { showEditBio = true }
            )

            ProfileEditDialogs(
                user = user!!,
                showEditName = showEditName,
                showEditBio = showEditBio,
                onDismissName = { showEditName = false },
                onDismissBio = { showEditBio = false },
                authVM = authVM,
                userProfileViewModel = userProfileViewModel
            )
        }

        if (isUpdating) {
            UpdateOverlay()
        }
    }
}

@Composable
private fun ProfileEffectHandlers(
    authVM: AuthViewModel,
    userProfileViewModel: UserProfileViewModel,
    galleryViewModel: GalleryViewModel
) {
    val context = LocalContext.current
    val authUser = authVM.currentUserProfile

    // Load user data
    LaunchedEffect(authUser?.userId) {
        authUser?.userId?.let { id ->
            userProfileViewModel.loadUser(id)
        }
    }

    // Image picking setup
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.use { it.readBytes() }
            if (bytes != null && authUser != null) {
                userProfileViewModel.updateAvatarPng(authUser.userId, bytes, authVM)
            }
        }
    }

    // Listen to gallery events
    LaunchedEffect(Unit) {
        galleryViewModel.events.collect { event ->
            if (event is GalleryEvent.OpenGallery) {
                pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    colors: ShowProfileColorPalette,
    authVM: AuthViewModel,
    userProfileViewModel: UserProfileViewModel,
    onEditAvatar: () -> Unit,
    onEditName: () -> Unit,
    onEditBio: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp)
    ) {
        ProfileHeader(colors)

        ProfileCard(
            user = user,
            home = colors,
            onEditAvatar = onEditAvatar,
            onEditName = onEditName
        )

        BioSection(
            user = user,
            home = colors,
            onEditBio = onEditBio
        )

        CoursesSection(
            home = colors,
            user = user,
            vm = userProfileViewModel,
            authVM = authVM
        )

        Spacer(Modifier.weight(1f))

        ProfileActions(authVM)
    }
}

@Composable
private fun ProfileEditDialogs(
    user: User,
    showEditName: Boolean,
    showEditBio: Boolean,
    onDismissName: () -> Unit,
    onDismissBio: () -> Unit,
    authVM: AuthViewModel,
    userProfileViewModel: UserProfileViewModel
) {
    if (showEditName) {
        EditNameDialog(
            currentName = user.name.orEmpty(),
            onDismiss = onDismissName,
            onSave = { newName ->
                userProfileViewModel.updateName(user.userId, newName, authVM)
                onDismissName()
            }
        )
    }

    if (showEditBio) {
        EditBioDialog(
            currentBio = user.bio,
            onDismiss = onDismissBio,
            onSave = { newBio ->
                userProfileViewModel.updateBio(user.userId, newBio, authVM)
                onDismissBio()
            }
        )
    }
}

@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun UpdateOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
    ) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}