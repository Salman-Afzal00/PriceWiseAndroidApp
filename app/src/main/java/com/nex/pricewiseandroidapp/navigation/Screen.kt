package com.nex.pricewiseandroidapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    // Auth Routes
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")

    // Main App Routes
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Chat : Screen("chat", "Chat", Icons.Default.ChatBubble)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)

    // Graph routes
    object AuthGraph : Screen("auth_graph")
    object MainGraph : Screen("main_graph")
}