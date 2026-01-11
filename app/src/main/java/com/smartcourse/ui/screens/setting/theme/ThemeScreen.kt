package com.smartcourse.ui.screens.setting.theme

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.smartcourse.ui.theme.ThemeMode


@Composable
fun ThemeScreen(
    navController: NavController
) {
    val viewModel: ThemeViewModel =
        hiltViewModel(LocalContext.current as ComponentActivity)

    val themeMode by viewModel.themeMode.collectAsState()

    Column {
        ThemeOption(
            title = "System",
            selected = themeMode == ThemeMode.SYSTEM,
            onClick = { viewModel.setTheme(ThemeMode.SYSTEM) }
        )
        ThemeOption(
            title = "Light",
            selected = themeMode == ThemeMode.LIGHT,
            onClick = { viewModel.setTheme(ThemeMode.LIGHT) }
        )
        ThemeOption(
            title = "Dark",
            selected = themeMode == ThemeMode.DARK,
            onClick = { viewModel.setTheme(ThemeMode.DARK) }
        )
    }
}


