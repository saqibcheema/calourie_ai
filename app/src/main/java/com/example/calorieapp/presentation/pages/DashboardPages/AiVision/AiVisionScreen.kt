package com.example.calorieapp.presentation.pages.DashboardPages.AiVision

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.calorieapp.presentation.pages.DashboardPages.AiVision.components.CameraCapture
import com.example.calorieapp.presentation.pages.DashboardPages.AiVision.components.DetectedItemsForm
import com.example.calorieapp.presentation.pages.DashboardPages.AiVision.components.GeminiLoadingOverlay
import com.example.calorieapp.presentation.pages.DashboardPages.ManualEntry.components.AiLoadingOverlay
import com.example.calorieapp.presentation.pages.DashboardPages.ManualEntry.components.AiResultsScreen
import com.example.calorieapp.presentation.viewModel.AiVisionPhase
import com.example.calorieapp.presentation.viewModel.AiVisionViewModel
import com.example.calorieapp.presentation.components.PremiumConnectivityStatus
import com.example.calorieapp.presentation.components.PremiumRateLimitStatus
import com.example.calorieapp.presentation.components.CameraPermissionHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier

@Composable
fun AiVisionScreen(
    onClose: () -> Unit,
    viewModel: AiVisionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Trigger close when success is set
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            viewModel.onClose()
            onClose()
        }
    }

    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null) {
            kotlinx.coroutines.delay(4000)
            viewModel.onDismissError()
        }
    }

    var hasCameraPermission by remember { mutableStateOf(false) }

    CameraPermissionHandler(
        onPermissionGranted = { hasCameraPermission = true },
        onClosed = { onClose() }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = state.phase,
            transitionSpec = {
                when {
                    // Moving forward: slide left
                    targetState.ordinal > initialState.ordinal ->
                        slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) togetherWith
                        slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(200))

                    // Moving backward: slide right
                    else ->
                        slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300)) togetherWith
                        slideOutHorizontally(tween(300)) { it } + fadeOut(tween(200))
                }
            },
            contentAlignment = Alignment.TopStart,
            label = "visionPhaseTransition"
        ) { phase ->
            when (phase) {
                AiVisionPhase.CAMERA -> {
                    if (hasCameraPermission) {
                        CameraCapture(
                            isOffline = state.isOffline,
                            errorMessage = state.errorMessage,
                            onPhotoCaptured = { bitmap -> viewModel.onPhotoCaptured(bitmap) },
                            onBackClick = {
                                viewModel.onClose()
                                onClose()
                            },
                            onErrorDismiss = { viewModel.onDismissError() }
                        )
                    } else {
                        // Show a black screen while permission is being handled by CameraPermissionHandler
                        Box(modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.Black))
                    }
                }

                AiVisionPhase.ANALYZING -> {
                    GeminiLoadingOverlay(capturedBitmap = state.capturedBitmap)
                }

                AiVisionPhase.QUANTITY_INPUT -> {
                    DetectedItemsForm(
                        capturedBitmap = state.capturedBitmap,
                        detectedItems = state.detectedItems,
                        itemPortions = state.itemPortions,
                        mealType = state.mealType,
                        eatingContext = state.eatingContext,
                        isClarificationNeeded = state.isClarificationNeeded,
                        clarificationQuestions = state.clarificationQuestions,
                        clarificationAnswers = state.clarificationAnswers,
                        errorMessage = state.errorMessage,
                        onMealTypeChange = { viewModel.onMealTypeChange(it) },
                        onEatingContextChange = { viewModel.onEatingContextChange(it) },
                        onPortionChanged = { name, portion -> viewModel.onPortionChanged(name, portion) },
                        onRemoveItem = { viewModel.onRemoveItem(it) },
                        onAddItem = { viewModel.onAddItem(it) },
                        onSubmit = { viewModel.onSubmitForEstimation() },
                        onClarificationAnswerChanged = { q, a -> viewModel.onClarificationAnswerChanged(q, a) },
                        onSubmitClarifications = { viewModel.submitClarifications() },
                        onCancelClarification = { viewModel.onCancelClarification() }
                    )
                }

                AiVisionPhase.ESTIMATING -> {
                    AiLoadingOverlay()
                }

                AiVisionPhase.RESULTS -> {
                    AiResultsScreen(
                        foodName = state.loggedFoodName,
                        calories = state.estimatedCalories,
                        protein = state.estimatedProtein,
                        carbs = state.estimatedCarbs,
                        fat = state.estimatedFat,
                        fiber = state.estimatedFiber,
                        sugars = state.estimatedSugars,
                        confidence = state.nutritionConfidence,
                        items = state.itemizedBreakdown,
                        onDone = { viewModel.dismissResults() }
                    )
                }
            }
        }

        PremiumConnectivityStatus(isOffline = state.isOffline)
        PremiumRateLimitStatus(message = state.errorMessage)
    }
}
