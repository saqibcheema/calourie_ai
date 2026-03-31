package com.example.calorieapp.presentation.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            TopHeader()
            Spacer(modifier = Modifier.height(24.dp))
            DateSelectorRow(
                selectedDate = selectedDate,
                onDateSelected = { date -> viewModel.updateSelectedDate(date) }
            )
            Spacer(modifier = Modifier.height(24.dp))
            CaloriesCard(leftCals = leftCals, progress = calProgress)
            Spacer(modifier = Modifier.height(16.dp))
            MacrosRow(
                leftProtein = leftProtein,
                proteinProgress = proteinProgress,
                leftCarbs = leftCarbs,
                carbsProgress = carbsProgress,
                leftFats = leftFats,
                fatsProgress = fatsProgress
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Recently uploaded",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            if (dailyMeals.isEmpty()) {
                RecentUploadPlaceholder()
            } else {
                dailyMeals.forEach { meal ->
                    MealItemRow(
                        product = meal,
                        onIncreaseQuantity = { viewModel.increaseQuantity(meal) },
                        onDecreaseOrDelete = { viewModel.decreaseQuantityOrDelete(meal) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(120.dp)) // Cushion for floating bottom dock
        }
    }
}
