package com.smartcourse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.navigation.RootNavigation
import com.smartcourse.ui.screens.setting.theme.ThemeViewModel
import com.smartcourse.ui.theme.SmartCourseTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeMode by themeViewModel.themeMode.collectAsState()

            CompositionLocalProvider(
                LocalLayoutDirection provides LayoutDirection.Ltr
            ) {
                SmartCourseTheme(
                    themeMode = themeMode
                ) {
                    RootNavigation(authViewModel)
                }
            }
        }
    }
}

