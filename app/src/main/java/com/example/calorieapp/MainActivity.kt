package com.example.calorieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.calorieapp.Core.CalorieAppNavigation
import com.example.calorieapp.presentation.viewModel.MainViewModel
import com.example.calorieapp.ui.theme.CalorieAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            viewModel.startDestination.value == null
        }
        enableEdgeToEdge()
        setContent {
            CalorieAppTheme {
                val startDest by viewModel.startDestination.collectAsState()

                startDest?.let { destination ->
                    CalorieAppNavigation(startDestination = destination)
                }
            }
        }
    }
}

