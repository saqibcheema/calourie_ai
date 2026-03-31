package com.example.calorieapp.Core

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calorieapp.presentation.pages.DashboardScreen
import com.example.calorieapp.presentation.pages.OnBoardingScreen
import com.example.calorieapp.presentation.pages.DashboardPages.ManualEntryScreen


import com.example.calorieapp.presentation.pages.MainScreen

@Composable
fun CalorieAppNavigation(
    startDestination : Any
){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ){
        composable<Dest.OnBoarding>{
            OnBoardingScreen(
                onNavigateToDashboard = {
                    navController.navigate(Dest.MainScreen){
                        popUpTo<Dest.OnBoarding>{
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable<Dest.MainScreen>{
            MainScreen(
                onNavigateToManualEntry = {
                    navController.navigate(Dest.ManualEntry)
                }
            )
        }
        composable<Dest.ManualEntry>{
            ManualEntryScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}


