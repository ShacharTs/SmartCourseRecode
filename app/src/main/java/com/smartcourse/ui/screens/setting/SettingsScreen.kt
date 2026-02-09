package com.smartcourse.ui.screens.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.navigation.Screen
import com.smartcourse.ui.theme.AppGradients
import com.smartcourse.ui.theme.LocalAppPalette


@Composable
fun SettingsScreen(
    navController: NavController,
    authVM: AuthViewModel = hiltViewModel(),
    settingVM: SettingViewModel = hiltViewModel()
) {
    val palette = LocalAppPalette.current
    val isDark = palette.isDark
    val gradientColors = if (isDark) AppGradients.Dark else AppGradients.Light

    // Using Box to ensure the background covers the entire screen including status bars
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SettingsHeader()

            SettingColumn(
                settingVM = settingVM,
                authVM = authVM,
                navController = navController
            )
        }
    }
}

@Composable
private fun SettingColumn(
    settingVM: SettingViewModel,
    authVM: AuthViewModel,
    navController: NavController
) {
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        SettingsSection(title = "Account") {
            SettingsRow(
                label = "Edit Profile",
                onClick = { navController.navigate(Screen.Profile.route) }
            )
            SettingsRow(
                label = "Delete Account",
                isDanger = true,
                onClick = { showDeleteAccountDialog = true }
            )
        }

        SettingsSection(title = "Notifications") {
            SettingsToggleRow(
                label = "Chat Notifications",
                isActive = settingVM.isChatEnabled,
                onToggle = { settingVM.toggleChat(it) }
            )
            SettingsToggleRow(
                label = "App Notifications",
                isActive = settingVM.isAppNotificationsEnabled,
                onToggle = { settingVM.toggleNotifications(it) }
            )
        }

        SettingsSection(title = "Preferences") {
            SettingsRow(
                label = "Theme",
                onClick = { navController.navigate(Screen.Theme.route) }
            )

            // todo  Dont have time for that sadly
//            SettingsRow(
//                label = "Language",
//                onClick = { navController.navigate(Screen.Language.route) }
//            )
        }

        SettingsSection(title = "About") {
            SettingsRow(
                label = "Terms & Privacy",
                onClick = { navController.navigate(Screen.Terms.route) }
            )
            SettingsRow(label = "App Version", value = "1.0.0")
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val settingsPalette = LocalAppPalette.current.settings

            // Consolidated button logic using a shared component
            SettingsActionButton(
                text = "Log out",
                textColor = settingsPalette.dangerText,
                onClick = { authVM.logout() }
            )

            SettingsActionButton(
                text = "Back",
                textColor = settingsPalette.headerText,
                onClick = { navController.popBackStack() }
            )
        }

        Spacer(Modifier.windowInsetsPadding(WindowInsets.navigationBars))
    }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Account") },
            text = {
                Text(
                    "To delete your account, please send an email to:\n\n" +
                            "iamNotGoingToCodeThat@gmail.com"
                )
            },
            confirmButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsHeader() {
    val palette = LocalAppPalette.current.settings
    // Using CenterAlignedTopAppBar with Transparent background to fix the top color bleed
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Settings",
                color = palette.headerText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.statusBarsPadding()
    )
}

/* ------------------ REUSABLE COMPONENTS ------------------ */

@Composable
fun SettingsActionButton(
    text: String,
    textColor: Color,
    onClick: () -> Unit
) {
    val palette = LocalAppPalette.current.settings
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(18.dp),
        color = palette.cardBackground,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = LocalAppPalette.current.settings
    Column {
        Text(
            text = title,
            color = palette.sectionTitle,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = palette.cardBackground
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                content = content
            )
        }
    }
}

@Composable
fun SettingsRow(
    label: String,
    isDanger: Boolean = false,
    value: String? = null,
    onClick: () -> Unit = {}
) {
    val palette = LocalAppPalette.current.settings
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = if (isDanger) palette.dangerText else palette.rowText,
            fontSize = 14.sp
        )
        if (value != null) {
            Text(text = value, color = palette.secondaryText, fontSize = 14.sp)
        } else {
            Text(
                text = "›",
                color = if (isDanger) palette.dangerText else palette.secondaryText,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun SettingsToggleRow(
    label: String,
    isActive: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val palette = LocalAppPalette.current.settings
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isActive) }
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = palette.rowText, fontSize = 14.sp)

        Surface(
            modifier = Modifier.size(width = 36.dp, height = 18.dp),
            shape = RoundedCornerShape(10.dp),
            color = if (isActive) palette.toggleTrackActive else palette.toggleTrackInactive
        ) {
            Box(contentAlignment = if (isActive) Alignment.CenterEnd else Alignment.CenterStart) {
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(12.dp),
                    shape = RoundedCornerShape(50),
                    color = if (isActive) palette.toggleThumbActive else palette.toggleThumbInactive
                ) {}
            }
        }
    }
}