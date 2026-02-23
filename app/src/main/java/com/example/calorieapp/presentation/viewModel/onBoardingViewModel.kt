package com.example.calorieapp.presentation.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorieapp.domain.entities.UserProfile
import com.example.calorieapp.domain.useCases.SaveUserAndCalculateGoalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor (
    private val saveUserUseCase : SaveUserAndCalculateGoalsUseCase
): ViewModel() {
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

    suspend fun onNext() {
        if(currentStep < totalSteps - 1 ) {
            currentStep++
        }else{
            saveAndFinish()
        }
    }
    fun onBack() {
        if(currentStep > 0 ) currentStep--
    }

    private suspend fun saveAndFinish(){
        viewModelScope.launch {
            val user = UserProfile(
                gender = gender,
                age = age,
                weight = weight.toString(),
                heightFeet = feetForHeight,
                heightInches = inchesForHeight,
                activityLevel = activityLevel,
                goal = goal
            )
            saveUserUseCase(user)
        }
    }
}