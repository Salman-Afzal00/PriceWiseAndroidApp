package com.nex.pricewiseandroidapp.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nex.pricewiseandroidapp.navigation.Screen

// A new composable that will host our main app content
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { MainNavigationBar(navController = navController) },
        containerColor = Color(0xFFF3F5F7) // BackgroundGray from your theme
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // Setup the NavHost for the main screens
            MainScreensNavHost(navController = navController)
        }
    }
}

@Composable
fun MainScreensNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            // Replace with your actual Home Screen
            PlaceholderScreen("Home Screen")
        }
        composable(Screen.Search.route) {
            // Replace with your actual Search Screen
            PlaceholderScreen("Search Screen")
        }
        composable(Screen.Profile.route) {
            // Here we use your existing ProfileScreen
            ProfileScreen(navController = navController)
        }
    }
}

// You can use this as a temporary screen until you build the others
@Composable
fun PlaceholderScreen(screenTitle: String) {
    Box(modifier = Modifier.padding(16.dp)) {
        Text(text = "This is the $screenTitle")
    }
}