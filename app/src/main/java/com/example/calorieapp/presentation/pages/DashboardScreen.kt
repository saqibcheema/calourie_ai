package com.example.calorieapp.presentation.pages

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calorieapp.presentation.pages.DashboardPages.*
import com.example.calorieapp.presentation.viewModel.DashboardViewModel
import com.example.calorieapp.ui.theme.GradientPink
import com.example.calorieapp.ui.theme.GradientBlue

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("NewApi")
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToManualEntry: () -> Unit = {}
) {
    val goals by viewModel.dailyGoals.collectAsStateWithLifecycle()
    val summary by viewModel.dailySummary.collectAsStateWithLifecycle()
    val dailyMeals by viewModel.dailyMeals.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val currentStreak by viewModel.currentStreak.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    val targetCals = goals?.calories ?: 0
    val targetProtein = goals?.protein ?: 0
    val targetCarbs = goals?.carbs ?: 0
    val targetFats = goals?.fats ?: 0

    val consumedCals = summary?.totalCalories ?: 0.0
    val consumedProtein = summary?.totalProtein ?: 0.0
    val consumedCarbs = summary?.totalCarbs ?: 0.0
    val consumedFats = summary?.totalFats ?: 0.0

    val leftCals = maxOf(0, targetCals - consumedCals.toInt())
    val leftProtein = maxOf(0, targetProtein - consumedProtein.toInt())
    val leftCarbs = maxOf(0, targetCarbs - consumedCarbs.toInt())
    val leftFats = maxOf(0, targetFats - consumedFats.toInt())

    val calProgress = if (targetCals > 0) consumedCals.toFloat() / targetCals else 0f
    val proteinProgress = if (targetProtein > 0) consumedProtein.toFloat() / targetProtein else 0f
    val carbsProgress = if (targetCarbs > 0) consumedCarbs.toFloat() / targetCarbs else 0f
    val fatsProgress = if (targetFats > 0) consumedFats.toFloat() / targetFats else 0f

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            GradientPink,
            GradientBlue,
            Color.White,
            Color.White
        )
    )

    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible.value = true }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val isTablet = screenWidth > 600.dp
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .statusBarsPadding()
                .padding(horizontal = if (isTablet) 48.dp else 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val columnModifier = if (isTablet) Modifier.widthIn(max = 600.dp) else Modifier.fillMaxWidth()
            
            Column(modifier = columnModifier) {
                Spacer(modifier = Modifier.height(16.dp))
                
                AnimatedVisibility(
                    visible = visible.value,
                    enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -20 }
                ) {
                    TopHeader(streak = currentStreak)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                AnimatedVisibility(
                    visible = visible.value,
                    enter = fadeIn(tween(400, delayMillis = 100)) + slideInVertically(tween(400, delayMillis = 100)) { 20 }
                ) {
                    DateSelectorRow(
                        selectedDate = selectedDate,
                        onDateSelected = { date -> viewModel.updateSelectedDate(date) }
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                AnimatedVisibility(
                    visible = visible.value,
                    enter = fadeIn(tween(400, delayMillis = 200)) + slideInVertically(tween(400, delayMillis = 200)) { 30 }
                ) {
                    CaloriesCard(leftCals = leftCals, progress = calProgress)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                AnimatedVisibility(
                    visible = visible.value,
                    enter = fadeIn(tween(400, delayMillis = 300)) + slideInVertically(tween(400, delayMillis = 300)) { 40 }
                ) {
                    MacrosRow(
                        leftProtein = leftProtein,
                        proteinProgress = proteinProgress,
                        leftCarbs = leftCarbs,
                        carbsProgress = carbsProgress,
                        leftFats = leftFats,
                        fatsProgress = fatsProgress
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                AnimatedVisibility(
                    visible = visible.value,
                    enter = fadeIn(tween(400, delayMillis = 400)) + slideInVertically(tween(400, delayMillis = 400)) { 50 }
                ) {
                    Text(
                        text = "Recently uploaded",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (dailyMeals.isEmpty()) {
                    AnimatedVisibility(
                        visible = visible.value,
                        enter = fadeIn(tween(400, delayMillis = 500))
                    ) {
                        RecentUploadPlaceholder()
                    }
                } else {
                    dailyMeals.forEachIndexed { index, meal ->
                        AnimatedVisibility(
                            visible = visible.value,
                            enter = fadeIn(tween(400, delayMillis = 500 + (index * 100))) + 
                                    slideInVertically(tween(400, delayMillis = 500 + (index * 100))) { 20 }
                        ) {
                            Column {
                                MealItemRow(
                                    product = meal,
                                    onIncreaseQuantity = { viewModel.increaseQuantity(meal) },
                                    onDecreaseOrDelete = { viewModel.decreaseQuantityOrDelete(meal) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(120.dp)) // Cushion for floating bottom dock
            }
        }
    }
}
