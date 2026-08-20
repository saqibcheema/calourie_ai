package com.example.calorieapp.presentation.viewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorieapp.domain.entities.UserProfile
import com.example.calorieapp.domain.useCases.CalculationUtils
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

    private var initialWeight: String = ""

    // ─── Existing fields ──────────────────────────────────────────────────────
    var gender by mutableStateOf("Male")
    var age by mutableIntStateOf(25)
    var weight by mutableStateOf("70")
    var feetForHeight by mutableIntStateOf(5)
    var inchesForHeight by mutableIntStateOf(8)
    var activityLevel by mutableStateOf("Moderate Activity")
    var goal by mutableStateOf("Maintain")

    // ─── Issue #4 Fix: New fields preserved when loading from DB ─────────────
    var goalPace by mutableStateOf("Moderate")
    var medicalConditions by mutableStateOf<List<String>>(emptyList())
    var pregnancyStatus by mutableStateOf(CalculationUtils.PREGNANCY_NONE)

    var isSaving by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            userRepository.getUser().collectLatest { profile ->
                profile?.let {
                    gender            = it.gender
                    age               = it.age
                    weight            = it.weight
                    initialWeight     = it.weight
                    feetForHeight     = it.heightFeet
                    inchesForHeight   = it.heightInches
                    activityLevel     = it.activityLevel
                    goal              = it.goal
                    // Issue #4 Fix: Load new fields so they are NOT lost on save
                    goalPace          = it.goalPace
                    medicalConditions = it.medicalConditions
                    pregnancyStatus   = it.pregnancyStatus
                }
            }
        }
    }

    fun saveProfile() {
        // Safe input validation to prevent crash or infinite loading on invalid weight
        val weightVal = weight.toFloatOrNull()
        if (weightVal == null || weightVal <= 0) {
            // Restore initial weight or fallback to safe default
            weight = if (initialWeight.toFloatOrNull() != null) initialWeight else "70"
        }

        viewModelScope.launch {
            isSaving = true
            try {
                val updatedProfile = UserProfile(
                    gender            = gender,
                    age               = age,
                    weight            = weight,
                    heightFeet        = feetForHeight,
                    heightInches      = inchesForHeight,
                    activityLevel     = activityLevel,
                    goal              = goal,
                    // Issue #4 Fix: All new fields included — no longer wiped on profile edit
                    goalPace          = goalPace,
                    medicalConditions = medicalConditions,
                    pregnancyStatus   = pregnancyStatus
                )
                saveUserUseCase(updatedProfile)
                initialWeight = weight // Update initial weight on successful save
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isSaving = false
            }
        }
    }
}
