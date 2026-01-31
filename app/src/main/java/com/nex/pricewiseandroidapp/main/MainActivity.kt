package com.nex.pricewiseandroidapp.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.nex.pricewiseandroidapp.auth.AuthViewModel
import com.nex.pricewiseandroidapp.navigation.RootNavGraph
import com.nex.pricewiseandroidapp.ui.theme.PriceWiseAndroidAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the splash screen
        installSplashScreen().setKeepOnScreenCondition {
            !authViewModel.isUserLoaded.value
        }

        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

        setContent {
            PriceWiseAndroidAppTheme {
                val navController = rememberNavController()
                RootNavGraph(navController = navController)
            }
        }
    }
}
