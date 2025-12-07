package com.smartcourse.navigation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import com.smartcourse.data.models.usermodel.UserRole
import com.smartcourse.navigation.Screen


fun bottomNavItemsForRole(role: UserRole): List<BottomNavItem> {
    return when (role) {
        UserRole.STUDENT -> listOf(
            BottomNavItem(Screen.UserRouter.route, "Home", Icons.Default.Home),
            BottomNavItem(Screen.ChatList.route, "Chats", Icons.AutoMirrored.Filled.Chat),
            BottomNavItem(Screen.SearchRouter.route, "Search\nTutors", Icons.Default.Search),
            BottomNavItem(Screen.Profile.route, "Profile", Icons.Default.Person),
        )
        UserRole.TUTOR -> listOf(
            BottomNavItem(Screen.UserRouter.route, "Home", Icons.Default.Home),
            BottomNavItem(Screen.ChatList.route, "Chats", Icons.AutoMirrored.Filled.Chat),
            BottomNavItem(Screen.SearchRouter.route, "Search\nStudents", Icons.Default.Search),
            BottomNavItem(Screen.Profile.route, "Profile", Icons.Default.Person),
        )
        UserRole.ADMIN -> listOf(
            BottomNavItem(Screen.UserRouter.route, "Admin", Icons.Default.Dashboard)
        )
        UserRole.TEMP -> emptyList()
    }
}

