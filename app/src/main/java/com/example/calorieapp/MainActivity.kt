package com.example.calorieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.calorieapp.presentation.pages.OnBoardingScreen
import com.example.calorieapp.ui.theme.CalorieAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalorieAppTheme {
                OnBoardingScreen()
            }
        }
    }
}

//@Preview
//@Composable
//fun CalorieAppPreview(){
//    CalorieAppTheme {
//        onBoardingScreen()
//    }
//}