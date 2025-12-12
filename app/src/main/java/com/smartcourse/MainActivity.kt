package com.smartcourse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.smartcourse.auth.AuthViewModel
import com.smartcourse.navigation.RootNavigation
import com.smartcourse.ui.theme.SmartCourseTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmartCourseTheme {
                RootNavigation(authViewModel)
            }
        }
    }
}
