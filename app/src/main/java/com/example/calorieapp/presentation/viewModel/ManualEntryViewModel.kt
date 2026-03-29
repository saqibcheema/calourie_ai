package com.example.calorieapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorieapp.data.DataSource.remote.dto.ClarificationQuestion
import com.example.calorieapp.data.DataSource.remote.dto.FoodItemEstimate
import com.example.calorieapp.domain.entities.Product
import com.example.calorieapp.domain.useCases.AddMealUseCase
import com.example.calorieapp.domain.useCases.EstimateNutritionUseCase
import com.example.calorieapp.domain.validation.ManualEntryValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class ManualEntryState(
    // ── User input ──────────────────────────────────────────────────────
    val mealDescription: String = "",
    val mealType: String = "Breakfast",              // Breakfast, Lunch, Dinner, Snack
    val eatingContext: String = "",                   // "", "Restaurant", "Home Cooked", "Street Food"

    // ── Clarification flow ──────────────────────────────────────────────
    val isClarificationNeeded: Boolean = false,
    val clarificationQuestions: List<ClarificationQuestion> = emptyList(),
    val clarificationAnswers: Map<String, String> = emptyMap(), // Maps question to user's answer

    // ── Loading / result state ──────────────────────────────────────────
    val isLoading: Boolean = false,
    val isEstimating: Boolean = false,
    val nutritionConfidence: String = "medium",
    val estimatedCalories: Double = 0.0,
    val estimatedProtein: Double = 0.0,
    val estimatedCarbs: Double = 0.0,
    val estimatedFat: Double = 0.0,
    val estimatedFiber: Double = 0.0,
    val estimatedSugars: Double = 0.0,
    val itemizedBreakdown: List<FoodItemEstimate> = emptyList(),
    val showResults: Boolean = false,
    val loggedFoodName: String = "",                  // AI-generated display name
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class ManualEntryViewModel @Inject constructor(
    private val addMealUseCase: AddMealUseCase,
    private val estimateNutritionUseCase: EstimateNutritionUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ManualEntryState())
    val state = _state.asStateFlow()

    fun onMealDescriptionChange(description: String) {
        _state.update { it.copy(mealDescription = description) }
    }

    fun onMealTypeChange(type: String) {
        _state.update { it.copy(mealType = type) }
    }

    fun onEatingContextChange(context: String) {
        // Toggle: tap again to deselect
        _state.update {
            it.copy(eatingContext = if (it.eatingContext == context) "" else context)
        }
    }

    fun onClarificationAnswerChanged(question: String, answer: String) {
        _state.update { 
            val newAnswers = it.clarificationAnswers.toMutableMap()
            newAnswers[question] = answer
            it.copy(clarificationAnswers = newAnswers)
        }
    }
    
    fun submitClarifications() {
        val currentState = _state.value
        
        // Combine answers into description
        val answersSummary = currentState.clarificationAnswers.values.filter { it.isNotBlank() }.joinToString(", ")
        val newDescription = if (answersSummary.isNotEmpty()) {
             "${currentState.mealDescription.trim()} (Clarifications: $answersSummary)"
        } else {
             currentState.mealDescription.trim()
        }

        _state.update {
            it.copy(
                mealDescription = newDescription,
                isClarificationNeeded = false,
                clarificationQuestions = emptyList(),
                clarificationAnswers = emptyMap()
            )
        }
        
        // Re-run the estimate process with the updated description containing the answers
        logFood()
    }
    
    fun onCancelClarification() {
        _state.update {
            it.copy(
                isClarificationNeeded = false,
                clarificationQuestions = emptyList(),
                clarificationAnswers = emptyMap()
            )
        }
    }

    fun logFood() {
        val currentState = _state.value

        val validationResult = ManualEntryValidator.validateDescription(
            description = currentState.mealDescription,
            mealType = currentState.mealType
        )

        when (validationResult) {
            is ManualEntryValidator.ValidationResult.Error -> {
                _state.update { it.copy(errorMessage = validationResult.message) }
            }
            is ManualEntryValidator.ValidationResult.Success -> {
                estimateAndSave(validationResult)
            }
        }
    }

    private fun estimateAndSave(validation: ManualEntryValidator.ValidationResult.Success) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isEstimating = true, errorMessage = null) }
            try {
                // Build natural-language prompt with optional context
                val userPrompt = buildString {
                    if (_state.value.eatingContext.isNotBlank()) {
                        append("[${_state.value.eatingContext}] ")
                    }
                    append(_state.value.mealType)
                    append(": ")
                    append(validation.description)
                }

                // Call Groq AI to estimate nutrition
                val estimationResult = estimateNutritionUseCase(userPrompt)

                _state.update { it.copy(isEstimating = false) }

                if (estimationResult.isFailure) {
                    val errorException = estimationResult.exceptionOrNull()
                    val errorMsg = when (errorException) {
                        is com.example.calorieapp.data.network.interceptors.NoConnectivityException -> 
                            "No internet connection. Please turn on Wi-Fi or mobile data."
                        is com.example.calorieapp.data.network.interceptors.RateLimitException -> 
                            "You're querying too fast! Please slow down and try again in a few seconds."
                        else -> 
                            "AI Estimation failed: ${errorException?.message ?: "Unknown AI error"}"
                    }
                    _state.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                    return@launch
                }

                val nutrition = estimationResult.getOrNull()

                // Handle Clarification if needed
                if (nutrition?.isClarificationNeeded == true && nutrition.clarificationQuestions.isNotEmpty()) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isClarificationNeeded = true,
                            clarificationQuestions = nutrition.clarificationQuestions,
                            clarificationAnswers = emptyMap() // Reset answers
                        )
                    }
                    return@launch
                }

                val confidence = nutrition?.confidence ?: "medium"
                val displayName = nutrition?.displayName ?: validation.description.take(30)
                val items = nutrition?.items ?: emptyList()

                val manualProduct = Product(
                    barcode = "manual_${System.currentTimeMillis()}",
                    productName = displayName,
                    brand = "Manual Entry",
                    imageUrl = null,
                    calories = nutrition?.calories ?: 0.0,
                    protein = nutrition?.protein ?: 0.0,
                    carbs = nutrition?.carbs ?: 0.0,
                    fat = nutrition?.fat ?: 0.0,
                    fiber = nutrition?.fiber ?: 0.0,
                    sugars = nutrition?.sugars ?: 0.0,
                    scannedAt = Date(),
                    quantity = 1
                )

                addMealUseCase(manualProduct)
                _state.update {
                    it.copy(
                        isLoading = false,
                        isEstimating = false,
                        showResults = true,
                        itemizedBreakdown = items,
                        loggedFoodName = displayName,
                        nutritionConfidence = confidence,
                        estimatedCalories = nutrition?.calories ?: 0.0,
                        estimatedProtein = nutrition?.protein ?: 0.0,
                        estimatedCarbs = nutrition?.carbs ?: 0.0,
                        estimatedFat = nutrition?.fat ?: 0.0,
                        estimatedFiber = nutrition?.fiber ?: 0.0,
                        estimatedSugars = nutrition?.sugars ?: 0.0
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, isEstimating = false, errorMessage = "Failed to log food: ${e.message}") }
            }
        }
    }

    fun dismissResults() {
        _state.update { it.copy(showResults = false, isSuccess = true) }
    }
}
