package com.example.calorieapp.presentation.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class OnBoardingViewModel : ViewModel() {
    var currentStep by mutableIntStateOf(0)
        private set

    var gender by mutableStateOf("Male")
    var age by mutableIntStateOf(25)
    var goal by mutableStateOf("Maintain")
    var feetForHeight by mutableIntStateOf(6)
    var inchesForHeight by mutableIntStateOf(0)
    var weight by mutableIntStateOf(70)
    var activityLevel by mutableStateOf("Low Activity")

    val totalSteps = 5

    fun onNext() {
        if(currentStep < totalSteps - 1 ) currentStep++
    }
    fun onBack() {
        if(currentStep > 0 ) currentStep--
    }
}