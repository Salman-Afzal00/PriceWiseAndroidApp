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
import com.nex.pricewiseandroidapp.auth.AuthViewModel
import com.nex.pricewiseandroidapp.navigation.MainNavigationBar
import com.nex.pricewiseandroidapp.navigation.Screen

@Composable
fun MainScreen(authViewModel: AuthViewModel) { // Accept shared ViewModel
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { MainNavigationBar(navController = navController) },
        containerColor = Color(0xFFF3F5F7) // BackgroundGray from your theme
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // Pass the shared ViewModel to the NavHost
            MainScreensNavHost(navController = navController, authViewModel = authViewModel)
        }
    }
}

@Composable
fun MainScreensNavHost(navController: NavHostController, authViewModel: AuthViewModel) { // Accept shared ViewModel
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Chat.route) {
            ChatScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            // Pass the shared ViewModel to the ProfileScreen
            ProfileScreen(navController = navController, authViewModel = authViewModel)
        }
    }
}

