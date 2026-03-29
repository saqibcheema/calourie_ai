package com.example.calorieapp.presentation.pages.DashboardPages

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.calorieapp.presentation.pages.DashboardPages.ManualEntry.components.*
import com.example.calorieapp.presentation.viewModel.ManualEntryViewModel
import com.example.calorieapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEntryScreen(
    onBackClick: () -> Unit,
    viewModel: ManualEntryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val placeholder = rememberRotatingPlaceholder()

    // Handle success navigation
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onBackClick()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Main form ────────────────────────────────────────────────────────
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Add Food", style = AppTypography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .imePadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Section 1: Meal Type Chips ───────────────────────────────
                Text(
                    text = "What meal is this?",
                    style = AppTypography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp)
                )
                MealTypeChipRow(
                    selected = state.mealType,
                    onSelect = { viewModel.onMealTypeChange(it) }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // ── Section 2: Main Text Input (hero element) ────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Describe your meal",
                                style = AppTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = CharcoalBlack
                            )
                        }

                        Text(
                            text = "Type everything you ate — AI will calculate the calories",
                            style = AppTypography.bodySmall,
                            color = SlateGrey,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = state.mealDescription,
                            onValueChange = { viewModel.onMealDescriptionChange(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp),
                            placeholder = {
                                Text(
                                    text = placeholder,
                                    style = AppTypography.bodyMedium,
                                    color = SlateGrey.copy(alpha = 0.6f),
                                    maxLines = 3
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            maxLines = 6,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = DisabledGrey,
                                focusedContainerColor = GhostWhite.copy(alpha = 0.5f),
                                unfocusedContainerColor = GhostWhite.copy(alpha = 0.3f)
                            ),
                            textStyle = AppTypography.bodyLarge
                        )
                    }
                }

                // ── Section 3: Accuracy Nudge ────────────────────────────────
                AccuracyNudgeBanner(description = state.mealDescription)

                // ── Section 4: Optional Context Chips ────────────────────────
                Text(
                    text = "Where did you eat? (optional)",
                    style = AppTypography.labelMedium,
                    color = SlateGrey,
                    modifier = Modifier.padding(start = 4.dp)
                )
                ContextChipRow(
                    selected = state.eatingContext,
                    onSelect = { viewModel.onEatingContextChange(it) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ── Clarification UI ─────────────────────────────────────────
                AnimatedVisibility(
                    visible = state.isClarificationNeeded,
                    enter = fadeIn(tween(300)) + expandVertically(tween(300, easing = FastOutSlowInEasing)),
                    exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
                ) {
                    ClarificationForm(
                        questions = state.clarificationQuestions,
                        answers = state.clarificationAnswers,
                        onAnswerChanged = { q, a -> viewModel.onClarificationAnswerChanged(q, a) },
                        onSubmit = { viewModel.submitClarifications() },
                        onCancel = { viewModel.onCancelClarification() },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // ── Section 5: Estimate Button ───────────────────────────────
                AnimatedVisibility(
                    visible = !state.isClarificationNeeded,
                    enter = fadeIn(), exit = fadeOut()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                    // AI hint text
                    AnimatedContent(
                        targetState = state.isEstimating,
                        transitionSpec = {
                            fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                        },
                        label = "aiHint"
                    ) { isEstimating ->
                        Text(
                            text = if (isEstimating) "Analyzing with LLama 3.3 AI..."
                                   else "AI will break down & calculate every item",
                            style = AppTypography.labelSmall,
                            color = if (isEstimating) MaterialTheme.colorScheme.secondary
                                    else MaterialTheme.colorScheme.primary,
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Gradient-style estimate button
                    Button(
                        onClick = { viewModel.logFood() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !state.isLoading && state.mealDescription.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = DisabledGrey
                        )
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Estimate & Log", style = AppTypography.titleMedium)
                        }
                    }
                }
                } // ADDED EXTRA BRACKET

                // ── Error message ────────────────────────────────────────────
                AnimatedVisibility(
                    visible = state.errorMessage != null,
                    enter = fadeIn(tween(300)) + expandVertically(),
                    exit = fadeOut(tween(200)) + shrinkVertically()
                ) {
                    state.errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = AppTypography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // ── AI Loading Overlay ───────────────────────────────────────────────
        AnimatedVisibility(
            visible = state.isEstimating,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AiLoadingOverlay()
        }

        // ── AI Results Screen ────────────────────────────────────────────────
        AnimatedVisibility(
            visible = state.showResults,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AiResultsScreen(
                foodName = state.loggedFoodName,
                calories  = state.estimatedCalories,
                protein   = state.estimatedProtein,
                carbs     = state.estimatedCarbs,
                fat       = state.estimatedFat,
                fiber     = state.estimatedFiber,
                sugars    = state.estimatedSugars,
                confidence = state.nutritionConfidence,
                items     = state.itemizedBreakdown,
                onDone    = { viewModel.dismissResults() }
            )
        }
    }
}
