package com.nex.pricewiseandroidapp.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun MainNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Chat,
        Screen.Profile
    )

    // Using your theme colors from Profile.kt
    val brandBlue = Color(0xFF137FEC)
    val textLight = Color(0xFF64748B)

    NavigationBar(
        containerColor = Color.White,
        contentColor = textLight
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                label = { Text(screen.title!!) },
                icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large back stack of destinations
                        // on the bottom bar.
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when re-selecting the same item
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = brandBlue,
                    selectedTextColor = brandBlue,
                    unselectedIconColor = textLight,
                    unselectedTextColor = textLight,
                    indicatorColor = Color(0xFFEFF6FF) // Light blue background for selected item
                )
            )
        }
    }
}
