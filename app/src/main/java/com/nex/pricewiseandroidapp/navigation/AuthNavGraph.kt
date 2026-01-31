package com.nex.pricewiseandroidapp.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.nex.pricewiseandroidapp.auth.forgot_password.ForgotPassword
import com.nex.pricewiseandroidapp.auth.login.LoginScreen
import com.nex.pricewiseandroidapp.auth.register.RegisterScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.Login.route,
        route = Screen.AuthGraph.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPassword(navController = navController)
        }
    }
}
