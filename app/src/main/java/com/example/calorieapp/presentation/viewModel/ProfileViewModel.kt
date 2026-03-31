package com.example.calorieapp.presentation.viewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorieapp.domain.entities.UserProfile
import com.example.calorieapp.domain.useCases.SaveUserAndCalculateGoalsUseCase
import com.example.calorieapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val saveUserUseCase: SaveUserAndCalculateGoalsUseCase
) : ViewModel() {

    var gender by mutableStateOf("Male")
    var age by mutableIntStateOf(25)
    var weight by mutableStateOf("70")
    var feetForHeight by mutableIntStateOf(5)
    var inchesForHeight by mutableIntStateOf(8)
    var activityLevel by mutableStateOf("Moderate Activity")
    var goal by mutableStateOf("Maintain")

    var isSaving by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            userRepository.getUser().collectLatest { profile ->
                profile?.let {
                    gender = it.gender
                    age = it.age
                    weight = it.weight
                    feetForHeight = it.heightFeet
                    inchesForHeight = it.heightInches
                    activityLevel = it.activityLevel
                    goal = it.goal
                }
            }
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            isSaving = true
            val updatedProfile = UserProfile(
                gender = gender,
                age = age,
                weight = weight,
                heightFeet = feetForHeight,
                heightInches = inchesForHeight,
                activityLevel = activityLevel,
                goal = goal
            )
            saveUserUseCase(updatedProfile)
            isSaving = false
        }
    }
}
