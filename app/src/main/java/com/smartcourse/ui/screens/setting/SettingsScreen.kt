package com.smartcourse.ui.screens.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
    authVM: AuthViewModel,
    settingVM: SettingViewModel = hiltViewModel()
) {

    val palette = LocalAppPalette.current
    val isDark = palette.isDark
    val gradientColors = if (isDark) AppGradients.Dark else AppGradients.Light

    var showDeleteAccountDialog by remember { mutableStateOf(false) }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SettingsHeader()

            SettingColumn(
                // Use state from ViewModel
                isChatEnabled = settingVM.isChatEnabled,
                isAppNotificationsEnabled = settingVM.isAppNotificationsEnabled,
                onChatToggle = { settingVM.toggleChat(it) },
                onNotificationToggle = { settingVM.toggleNotifications(it) },
                authVM = authVM,
                navController = navController
            )
        }
    }
}

@Composable
private fun SettingColumn(
    isChatEnabled: Boolean,
    isAppNotificationsEnabled: Boolean,
    onChatToggle: (Boolean) -> Unit,
    onNotificationToggle: (Boolean) -> Unit,
    authVM: AuthViewModel,
    navController: NavController
) {
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        SettingsSection(title = "Account") {

            SettingsRow(
                label = "Edit Profile",
                onClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )

            SettingsRow(
                label = "Delete Account",
                isDanger = true,
                onClick = {
                    showDeleteAccountDialog = true
                }
            )
        }

        SettingsSection(title = "Notifications") {
            SettingsToggleRow(
                label = "Chat Notifications",
                isActive = isChatEnabled,
                onToggle = onChatToggle
            )
            SettingsToggleRow(
                label = "App Notifications",
                isActive = isAppNotificationsEnabled,
                onToggle = onNotificationToggle
            )
        }

        SettingsSection(title = "Preferences") {
            SettingsRow(
                label = "Theme",
                onClick = { navController.navigate(Screen.Theme.route) }
            )
            SettingsRow(
                label = "Language",
                onClick = { navController.navigate(Screen.Language.route) }
            )
        }

        SettingsSection(title = "About") {
            SettingsRow(
                label = "Terms & Privacy",
                onClick = { navController.navigate(Screen.Terms.route) }
            )
            SettingsRow(label = "App Version", value = "1.0.0")
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LogoutButton { authVM.logout() }
            BackButton { navController.popBackStack() }
        }
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
                TextButton(
                    onClick = { showDeleteAccountDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}


@Composable
fun SettingsHeader() {
    val palette = LocalAppPalette.current.settings
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(72.dp),
        color = palette.headerBackground
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "Settings",
                color = palette.headerText,
                fontSize = 22.sp,
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
    onClick: () -> Unit = {} // Add the onClick parameter with a default empty action
) {
    val palette = LocalAppPalette.current.settings
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Apply clickable before padding so the whole row area is touch-responsive
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
            .clickable { onToggle(!isActive) } // Flip the current value
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
            // Alignment moves the thumb left or right based on isActive
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

@Composable
fun LogoutButton(onClick: () -> Unit) {
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
                text = "Log out",
                color = palette.dangerText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BackButton(onClick: () -> Unit) {
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
                text = "Back",
                color = palette.headerText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

    }
}