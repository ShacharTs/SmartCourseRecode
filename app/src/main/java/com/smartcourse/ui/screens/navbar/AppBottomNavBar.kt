package com.smartcourse.ui.screens.navbar

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.smartcourse.ui.theme.NavBarColors



@Composable
fun AppBottomNavBar(
    navController: NavController,
    items: List<BottomNavItem>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (items.isEmpty()) return

    // Screen-specific nav bar colors
    val colors = if (isSystemInDarkTheme()) {
        NavBarColors.Dark
    } else {
        NavBarColors.Light
    }

    NavigationBar(
        containerColor = colors.background
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selected)
                            colors.iconSelected
                        else
                            colors.iconUnselected
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (selected)
                            colors.textSelected
                        else
                            colors.textUnselected
                    )
                }
            )
        }
    }
}