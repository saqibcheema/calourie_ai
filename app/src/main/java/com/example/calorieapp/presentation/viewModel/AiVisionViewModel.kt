package com.example.calorieapp.presentation.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorieapp.data.DataSource.remote.dto.ClarificationQuestion
import com.example.calorieapp.data.DataSource.remote.dto.DetectedFoodItem
import com.example.calorieapp.data.DataSource.remote.dto.FoodItemEstimate
import com.example.calorieapp.domain.entities.Product
import com.example.calorieapp.domain.useCases.AddMealUseCase
import com.example.calorieapp.domain.useCases.AnalyzeFoodImageUseCase
import com.example.calorieapp.domain.useCases.EstimateNutritionUseCase
import com.example.calorieapp.util.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

enum class AiVisionPhase {
    CAMERA,
    ANALYZING,
    QUANTITY_INPUT,
    ESTIMATING,
    RESULTS
}

data class AiVisionState(
    val phase: AiVisionPhase = AiVisionPhase.CAMERA,

    // Connectivity
    val isOffline: Boolean = false,

    // Camera
    val capturedBitmap: Bitmap? = null,

    // Gemini analysis
    val isAnalyzing: Boolean = false,
    val detectedItems: List<DetectedFoodItem> = emptyList(),

    // Quantity input
    val mealType: String = "Lunch",
    val eatingContext: String = "",
    val itemPortions: Map<String, String> = emptyMap(),

    // Clarification (same pattern as ManualEntryViewModel)
    val isClarificationNeeded: Boolean = false,
    val clarificationQuestions: List<ClarificationQuestion> = emptyList(),
    val clarificationAnswers: Map<String, String> = emptyMap(),

    // Groq estimation results
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
    val loggedFoodName: String = "",

    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AiVisionViewModel @Inject constructor(
    private val analyzeFoodImageUseCase: AnalyzeFoodImageUseCase,
    private val estimateNutritionUseCase: EstimateNutritionUseCase,
    private val addMealUseCase: AddMealUseCase,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _state = MutableStateFlow(AiVisionState())
    val state = _state.asStateFlow()

    init {
        observeConnectivity()
    }

    private fun observeConnectivity() {
        connectivityObserver.observe()
            .onEach { status ->
                _state.update {
                    it.copy(isOffline = status != ConnectivityObserver.Status.Available)
                }
            }
            .launchIn(viewModelScope)
    }

    // ── Camera ───────────────────────────────────────────────────────────────

    fun onPhotoCaptured(bitmap: Bitmap) {
        if (_state.value.isOffline) {
            _state.update { it.copy(errorMessage = "No internet connection. Please turn on Wi-Fi or mobile data.") }
            return
        }

        val compressed = compressBitmap(bitmap)
        _state.update {
            it.copy(
                capturedBitmap = compressed,
                phase = AiVisionPhase.ANALYZING,
                isAnalyzing = true,
                errorMessage = null
            )
        }
        analyzePhoto(compressed)
    }

    fun retryPhoto() {
        _state.value.capturedBitmap?.recycle()
        _state.update {
            it.copy(
                phase = AiVisionPhase.CAMERA,
                capturedBitmap = null,
                isAnalyzing = false,
                detectedItems = emptyList(),
                errorMessage = null
            )
        }
    }

    fun onDismissError() {
        _state.update { it.copy(errorMessage = null) }
    }

    // ── Gemini Analysis ───────────────────────────────────────────────────────

    private fun analyzePhoto(bitmap: Bitmap) {
        viewModelScope.launch {
            val result = analyzeFoodImageUseCase(bitmap)

            if (result.isFailure) {
                _state.update {
                    it.copy(
                        isAnalyzing = false,
                        phase = AiVisionPhase.CAMERA,
                        errorMessage = result.exceptionOrNull()?.message
                            ?: "Failed to analyze image. Please try again."
                    )
                }
                return@launch
            }

            val foodResult = result.getOrNull()!!
            _state.update {
                it.copy(
                    isAnalyzing = false,
                    detectedItems = foodResult.items,
                    // Pre-fill portions from Gemini's estimations if available
                    itemPortions = foodResult.items.associate { item ->
                        item.name to (item.estimatedPortion ?: "")
                    },
                    phase = AiVisionPhase.QUANTITY_INPUT
                )
            }
        }
    }

    // ── Quantity Input ────────────────────────────────────────────────────────

    fun onMealTypeChange(type: String) {
        _state.update { it.copy(mealType = type) }
    }

    fun onEatingContextChange(context: String) {
        _state.update {
            it.copy(eatingContext = if (it.eatingContext == context) "" else context)
        }
    }

    fun onPortionChanged(itemName: String, portion: String) {
        _state.update {
            val updated = it.itemPortions.toMutableMap()
            updated[itemName] = portion
            it.copy(itemPortions = updated)
        }
    }

    fun onRemoveItem(itemName: String) {
        _state.update {
            val updatedItems = it.detectedItems.filter { item -> item.name != itemName }
            val updatedPortions = it.itemPortions.toMutableMap().also { map -> map.remove(itemName) }
            it.copy(detectedItems = updatedItems, itemPortions = updatedPortions)
        }
    }

    fun onAddItem(itemName: String) {
        if (itemName.isBlank()) return
        val newItem = DetectedFoodItem(name = itemName.trim())
        _state.update {
            it.copy(
                detectedItems = it.detectedItems + newItem,
                itemPortions = it.itemPortions + (newItem.name to "")
            )
        }
    }

    // ── Estimation ────────────────────────────────────────────────────────────

    fun onSubmitForEstimation() {
        val currentState = _state.value

        // Build natural-language prompt combining detected items + user-provided portions
        val foodDescription = buildString {
            if (currentState.eatingContext.isNotBlank()) {
                append("[${currentState.eatingContext}] ")
            }
            append(currentState.mealType)
            append(": ")
            val itemDescriptions = currentState.detectedItems.joinToString(", ") { item ->
                val portion = currentState.itemPortions[item.name]?.trim() ?: ""
                if (portion.isNotBlank()) "$portion ${item.name}" else item.name
            }
            append(itemDescriptions)
        }

        viewModelScope.launch {
            _state.update { it.copy(phase = AiVisionPhase.ESTIMATING, isEstimating = true, errorMessage = null) }

            val estimationResult = estimateNutritionUseCase(foodDescription)

            if (estimationResult.isFailure) {
                val errorException = estimationResult.exceptionOrNull()
                val errorMsg = when (errorException) {
                    is com.example.calorieapp.data.network.interceptors.NoConnectivityException ->
                        "No internet connection. Please turn on Wi-Fi or mobile data."
                    is com.example.calorieapp.data.network.interceptors.RateLimitException ->
                        "You're querying too fast! Please slow down and try again."
                    else ->
                        "Nutrition estimation failed: ${errorException?.message ?: "Unknown error"}"
                }
                _state.update {
                    it.copy(
                        isEstimating = false,
                        phase = AiVisionPhase.QUANTITY_INPUT,
                        errorMessage = errorMsg
                    )
                }
                return@launch
            }

            val nutrition = estimationResult.getOrNull()

            // Handle clarification needed (same pattern as ManualEntryViewModel)
            if (nutrition?.isClarificationNeeded == true && nutrition.clarificationQuestions.isNotEmpty()) {
                _state.update {
                    it.copy(
                        isEstimating = false,
                        phase = AiVisionPhase.QUANTITY_INPUT,
                        isClarificationNeeded = true,
                        clarificationQuestions = nutrition.clarificationQuestions,
                        clarificationAnswers = emptyMap()
                    )
                }
                return@launch
            }

            val displayName = nutrition?.displayName ?: "AI Detected Meal"
            val items = nutrition?.items ?: emptyList()

            val product = Product(
                barcode = "vision_${System.currentTimeMillis()}",
                productName = displayName,
                brand = "AI Vision",
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

            addMealUseCase(product)

            // Free bitmap memory after save
            _state.value.capturedBitmap?.recycle()

            _state.update {
                it.copy(
                    isEstimating = false,
                    phase = AiVisionPhase.RESULTS,
                    capturedBitmap = null,
                    showResults = true,
                    loggedFoodName = displayName,
                    nutritionConfidence = nutrition?.confidence ?: "medium",
                    estimatedCalories = nutrition?.calories ?: 0.0,
                    estimatedProtein = nutrition?.protein ?: 0.0,
                    estimatedCarbs = nutrition?.carbs ?: 0.0,
                    estimatedFat = nutrition?.fat ?: 0.0,
                    estimatedFiber = nutrition?.fiber ?: 0.0,
                    estimatedSugars = nutrition?.sugars ?: 0.0,
                    itemizedBreakdown = items
                )
            }
        }
    }

    // ── Clarification ──────────────────────────────────────────────────────────

    fun onClarificationAnswerChanged(question: String, answer: String) {
        _state.update {
            val updated = it.clarificationAnswers.toMutableMap()
            updated[question] = answer
            it.copy(clarificationAnswers = updated)
        }
    }

    fun submitClarifications() {
        val currentState = _state.value
        val answersSummary = currentState.clarificationAnswers.values
            .filter { it.isNotBlank() }
            .joinToString(", ")

        // Append clarifications to the item descriptions and re-estimate
        if (answersSummary.isNotBlank()) {
            val firstItem = currentState.detectedItems.firstOrNull()
            if (firstItem != null) {
                val currentPortion = currentState.itemPortions[firstItem.name] ?: ""
                _state.update {
                    val updated = it.itemPortions.toMutableMap()
                    updated[firstItem.name] = "$currentPortion ($answersSummary)"
                    it.copy(
                        itemPortions = updated,
                        isClarificationNeeded = false,
                        clarificationQuestions = emptyList(),
                        clarificationAnswers = emptyMap()
                    )
                }
            }
        } else {
            _state.update {
                it.copy(
                    isClarificationNeeded = false,
                    clarificationQuestions = emptyList(),
                    clarificationAnswers = emptyMap()
                )
            }
        }
        onSubmitForEstimation()
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

    // ── Results ───────────────────────────────────────────────────────────────

    fun dismissResults() {
        _state.update { it.copy(showResults = false, isSuccess = true) }
    }

    fun onClose() {
        _state.value.capturedBitmap?.recycle()
        _state.update { AiVisionState() }
    }

    // ── Utilities ──────────────────────────────────────────────────────────────

    private fun compressBitmap(original: Bitmap): Bitmap {
        val maxSize = 1024
        val ratio = minOf(
            maxSize.toFloat() / original.width,
            maxSize.toFloat() / original.height
        )
        return if (ratio >= 1f) {
            original
        } else {
            Bitmap.createScaledBitmap(
                original,
                (original.width * ratio).toInt(),
                (original.height * ratio).toInt(),
                true
            ).also { if (it != original) original.recycle() }
        }
    }
}
