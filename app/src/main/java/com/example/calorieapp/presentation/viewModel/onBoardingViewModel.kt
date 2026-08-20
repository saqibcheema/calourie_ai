package com.example.calorieapp.presentation.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorieapp.domain.entities.UserProfile
import com.example.calorieapp.domain.useCases.CalculationUtils
import com.example.calorieapp.domain.useCases.SaveUserAndCalculateGoalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val saveUserUseCase: SaveUserAndCalculateGoalsUseCase
) : ViewModel() {

    // ─── Existing fields ───────────────────────────────────────────────────────
    var gender by mutableStateOf("Male")
    var age by mutableIntStateOf(25)
    var goal by mutableStateOf("Maintain")
    var feetForHeight by mutableIntStateOf(6)
    var inchesForHeight by mutableIntStateOf(0)
    var weight by mutableIntStateOf(70)
    var activityLevel by mutableStateOf("Low Activity")

    // ─── New fields ────────────────────────────────────────────────────────────
    var goalPace by mutableStateOf("Moderate")
    var medicalConditions by mutableStateOf<List<String>>(emptyList())
    var pregnancyStatus by mutableStateOf(CalculationUtils.PREGNANCY_NONE)

    // ─── Step Management ──────────────────────────────────────────────────────
    var currentStep by mutableIntStateOf(0)
        private set

    /**
     * Dynamic total steps:
     * Base: Gender(0) Age(1) Height/Weight(2) Activity(3) Goal(4)
     * + GoalPace(5) if goal != Maintain
     * + Medical(5 or 6)
     * + Pregnancy(6 or 7) if gender == Female
     */
    val totalSteps: Int
        get() {
            var steps = 6 // base: 0..4 + MedicalConditions always shown
            if (goal != "Maintain") steps++  // GoalPace screen
            if (gender.equals("Female", ignoreCase = true)) steps++ // Pregnancy screen
            return steps
        }

    fun onNext(onNavigate: () -> Unit = {}) {
        val nextStep = getNextStep(currentStep)
        if (nextStep == null) {
            // We're done — save and navigate
            viewModelScope.launch {
                saveAndFinish()
                onNavigate()
            }
        } else {
            currentStep = nextStep
        }
    }

    fun onBack() {
        val prevStep = getPrevStep(currentStep)
        if (prevStep != null) currentStep = prevStep
    }

    /**
     * Step routing logic:
     * 0 = Gender
     * 1 = Age
     * 2 = Height/Weight
     * 3 = Activity
     * 4 = Goal
     * 5 = GoalPace  (only if goal != Maintain)
     * 6 = Medical Conditions (always, step index shifts based on GoalPace)
     * 7 = Pregnancy (only if Female, index shifts)
     */
    private val STEP_GENDER   = 0
    private val STEP_AGE      = 1
    private val STEP_BODY     = 2
    private val STEP_ACTIVITY = 3
    private val STEP_GOAL     = 4
    private val STEP_PACE     = 5  // Only if goal != Maintain
    val STEP_MEDICAL: Int get() = if (goal != "Maintain") 6 else 5
    val STEP_PREGNANCY: Int get() = STEP_MEDICAL + 1

    fun getScreenForStep(step: Int): OnboardingStep = when {
        step == STEP_GENDER   -> OnboardingStep.GENDER
        step == STEP_AGE      -> OnboardingStep.AGE
        step == STEP_BODY     -> OnboardingStep.BODY
        step == STEP_ACTIVITY -> OnboardingStep.ACTIVITY
        step == STEP_GOAL     -> OnboardingStep.GOAL
        step == STEP_PACE && goal != "Maintain" -> OnboardingStep.GOAL_PACE
        step == STEP_MEDICAL  -> OnboardingStep.MEDICAL
        step == STEP_PREGNANCY && gender.equals("Female", ignoreCase = true) -> OnboardingStep.PREGNANCY
        else                  -> OnboardingStep.MEDICAL // fallback
    }

    private fun getNextStep(current: Int): Int? {
        return when (current) {
            STEP_GENDER   -> STEP_AGE
            STEP_AGE      -> STEP_BODY
            STEP_BODY     -> STEP_ACTIVITY
            STEP_ACTIVITY -> STEP_GOAL
            STEP_GOAL     -> if (goal != "Maintain") STEP_PACE else STEP_MEDICAL
            STEP_PACE     -> STEP_MEDICAL
            STEP_MEDICAL  -> {
                if (gender.equals("Female", ignoreCase = true)) STEP_PREGNANCY
                else null // Done!
            }
            STEP_PREGNANCY -> null // Done!
            else -> null
        }
    }

    private fun getPrevStep(current: Int): Int? {
        return when (current) {
            STEP_GENDER   -> null
            STEP_AGE      -> STEP_GENDER
            STEP_BODY     -> STEP_AGE
            STEP_ACTIVITY -> STEP_BODY
            STEP_GOAL     -> STEP_ACTIVITY
            STEP_PACE     -> STEP_GOAL
            STEP_MEDICAL  -> if (goal != "Maintain") STEP_PACE else STEP_GOAL
            STEP_PREGNANCY -> STEP_MEDICAL
            else -> null
        }
    }

    private suspend fun saveAndFinish() {
        val user = UserProfile(
            gender            = gender,
            age               = age,
            weight            = weight.toString(),
            heightFeet        = feetForHeight,
            heightInches      = inchesForHeight,
            activityLevel     = activityLevel,
            goal              = goal,
            goalPace          = goalPace,
            medicalConditions = medicalConditions,
            pregnancyStatus   = pregnancyStatus
        )
        saveUserUseCase(user)
    }
}

enum class OnboardingStep {
    GENDER, AGE, BODY, ACTIVITY, GOAL, GOAL_PACE, MEDICAL, PREGNANCY
}