package com.smartcourse.ui.screens.navbar

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomNavBar(
    navController: NavController,
    items: List<BottomNavItem>
) {
    // Get the current route to determine which item is selected
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Show the navigation bar only if there are items for the current role
    if (items.isNotEmpty()) {
        NavigationBar {
            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    // Check if the current route matches the item's route
                    selected = currentRoute?.contains(item.route) == true,
                    onClick = {
                        // Standard navigation logic for Bottom Bar
                        navController.navigate(item.route) {
                            // Avoid building up a large stack of destinations
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            // Avoid multiple copies of the same destination when reselecting
                            launchSingleTop = true
                            // Restore state when reselecting
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}