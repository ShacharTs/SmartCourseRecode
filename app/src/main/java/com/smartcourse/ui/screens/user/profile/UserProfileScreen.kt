package com.smartcourse.ui.screens.user.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.ui.screens.chat.vm.GalleryEvent
import com.smartcourse.ui.screens.chat.vm.GalleryViewModel
import com.smartcourse.ui.screens.loading.LoadingScreen
import com.smartcourse.ui.screens.user.profile.components.*
import com.smartcourse.ui.theme.LocalAppPalette
import com.smartcourse.ui.theme.ShowProfileLayoutColors

@Composable
fun UserProfileScreen(
    navController: NavController,
    authVM: AuthViewModel = hiltViewModel(),
    vm: UserProfileViewModel = hiltViewModel(),
    galleryVM: GalleryViewModel = hiltViewModel()
) {
    val palette = LocalAppPalette.current
    val colors = if (palette.isDark) ShowProfileLayoutColors.Dark else ShowProfileLayoutColors.Light

    val user by vm.user.collectAsStateWithLifecycle()
    val isUpdating by vm.isUpdating.collectAsStateWithLifecycle()
    val authUser = authVM.currentUserProfile
    val context = LocalContext.current

    var dialogState by remember { mutableStateOf<String?>(null) } // "name", "bio", or null

    // Handlers
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.use { it.readBytes() }
            if (bytes != null && authUser != null) vm.updateAvatarPng(authUser.userId, bytes, authVM)
        }
    }

    LaunchedEffect(authUser?.userId) { authUser?.userId?.let { vm.loadUser(it) } }
    LaunchedEffect(Unit) { galleryVM.events.collect { if (it is GalleryEvent.OpenGallery) launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) } }

    if (authUser == null || user == null) { LoadingScreen(); return }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors.backgroundGradient))) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState()).padding(bottom = 24.dp)
        ) {
            ProfileHeader(colors)
            ProfileCard(user!!, colors, onEditAvatar = { galleryVM.requestGallery() }, onEditName = { dialogState = "name" })
            BioSection(user!!, colors, onEditBio = { dialogState = "bio" })
            CoursesSection(colors, user!!, vm, authVM)

            Spacer(Modifier.height(24.dp))

            // Reusing your new standard button style
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileActionButton(text = "Back", onClick = { navController.popBackStack() })
                ProfileActionButton(text = "Log out", isDanger = true, onClick = { authVM.logout() })
            }
        }

        if (isUpdating) Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface.copy(0.4f))) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }

        // Consolidated Dialog Logic
        when (dialogState) {
            "name" -> EditNameDialog(user!!.name.orEmpty(), onDismiss = { dialogState = null }, onSave = { vm.updateName(user!!.userId, it, authVM); dialogState = null })
            "bio" -> EditBioDialog(user!!.bio, onDismiss = { dialogState = null }, onSave = { vm.updateBio(user!!.userId, it, authVM); dialogState = null })
        }
    }
}

@Composable
private fun ProfileActionButton(text: String, isDanger: Boolean = false, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isDanger) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(0.1f),
            contentColor = if (isDanger) Color.Red else Color.White
        ),
        border = if (isDanger) androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(0.5f)) else null
    ) {
        Text(text, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}