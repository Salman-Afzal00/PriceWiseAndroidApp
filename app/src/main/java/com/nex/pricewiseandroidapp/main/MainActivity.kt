package com.nex.pricewiseandroidapp.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.nex.pricewiseandroidapp.auth.forgot_password.ForgotPassword
import com.nex.pricewiseandroidapp.auth.login.LoginScreen
import com.nex.pricewiseandroidapp.auth.register.RegisterScreen
import com.nex.pricewiseandroidapp.ui.theme.PriceWiseAndroidAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContent {
            PriceWiseAndroidAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(navController = navController)
                    }
                    composable("register") {
                        RegisterScreen(navController = navController)
                    }
                    composable("home") {
                        Home(navController = navController)
                    }
                    composable("forgot_password") {
                        ForgotPassword(navController = navController)
                    }
                    composable("profile") {
                        ProfileScreen(navController = navController)
                    }
                }
            }
        }
    }
}