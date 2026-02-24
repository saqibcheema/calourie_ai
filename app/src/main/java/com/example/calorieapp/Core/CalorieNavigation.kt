package com.example.calorieapp.Core

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calorieapp.presentation.pages.DashBoardScreen
import com.example.calorieapp.presentation.pages.OnBoardingScreen


@Composable
fun CalorieAppNavigation(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Dest.OnBoarding
    ){
        composable<Dest.OnBoarding>{
            OnBoardingScreen(
                onNavigateToDashboard = {
                    navController.navigate(Dest.Dashboard){
                        popUpTo<Dest.Dashboard>{
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable<Dest.Dashboard>{
            DashBoardScreen()
        }
    }
}