package com.nex.pricewiseandroidapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nex.pricewiseandroidapp.auth.AuthViewModel
import com.nex.pricewiseandroidapp.auth.AuthState
import com.nex.pricewiseandroidapp.main.MainScreen

@Composable
fun RootNavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    // This is now safe because the graph is only composed after the user is loaded.
    val startDestination = if (authViewModel.isEmailVerified()) {
        Screen.MainGraph.route
    } else {
        Screen.AuthGraph.route
    }

    // Listen for the UserLoggedOut state to navigate back to the auth graph
    LaunchedEffect(authState) {
        if (authState is AuthState.UserLoggedOut) {
            navController.navigate(Screen.AuthGraph.route) {
                // Clear the entire back stack
                popUpTo(navController.graph.id) { inclusive = true }
            }
            authViewModel.resetAuthState()
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authNavGraph(navController = navController)
        composable(route = Screen.MainGraph.route) {
            // Pass the single, shared ViewModel instance to the MainScreen
            MainScreen(authViewModel = authViewModel)
        }
    }
}
