package com.smartcourse.ui.screens.setting.language

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartcourse.ui.screens.setting.theme.ThemeViewModel
import com.smartcourse.ui.theme.ThemeMode
import com.smartcourse.ui.theme.ThemeScreenColorPalette
import com.smartcourse.ui.theme.ThemeScreenColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(navController: NavController) {
    val activity = LocalContext.current as ComponentActivity
    val themeViewModel: ThemeViewModel = hiltViewModel(activity)
    val languageViewModel: LanguageViewModel = hiltViewModel(activity)

    val themeMode by themeViewModel.themeMode.collectAsState()
    val currentLanguage by languageViewModel.languageCode.collectAsState()
    val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()

    val colors = remember(themeMode, isSystemDark) {
        when (themeMode) {
            ThemeMode.LIGHT -> ThemeScreenColors.Light
            ThemeMode.DARK -> ThemeScreenColors.Dark
            ThemeMode.SYSTEM -> if (isSystemDark) ThemeScreenColors.Dark else ThemeScreenColors.Light
        }
    }

    // מיפוי קוד השפה לשם התצוגה
    val languageNames = mapOf(
        "en" to "English",
        "iw" to "Hebrew"
    )

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text("Language / שפה", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.topBarBackground,
                    titleContentColor = colors.topBarContent,
                    navigationIconContentColor = colors.topBarContent
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Text(
                text = "Choose your preferred language",
                style = MaterialTheme.typography.labelLarge,
                color = colors.textSecondary,
                modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
            )

            val options = listOf(
                "English" to "en",
                "עברית" to "iw"
            )

            options.forEach { (label, code) ->
                LanguageOptionItem(
                    title = label,
                    selected = currentLanguage == code,
                    colors = colors,
                    onClick = { languageViewModel.setLanguage(code) }
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Current language is \"${languageNames[currentLanguage] ?: currentLanguage}\"",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun LanguageOptionItem(
    title: String,
    selected: Boolean,
    colors: ThemeScreenColorPalette,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) colors.cardSelectedBackground else colors.cardBackground,
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) colors.accent else colors.textSecondary.copy(alpha = 0.2f)
        ),
        shadowElevation = if (selected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = if (selected) colors.accent else colors.iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            RadioButton(
                selected = selected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = colors.accent,
                    unselectedColor = colors.textSecondary
                )
            )
        }
    }
}