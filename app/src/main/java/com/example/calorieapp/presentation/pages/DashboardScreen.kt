package com.example.calorieapp.presentation.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calorieapp.presentation.pages.DashboardPages.CaloriesCard
import com.example.calorieapp.presentation.pages.DashboardPages.DateSelectorRow
import com.example.calorieapp.presentation.pages.DashboardPages.MacrosRow
import com.example.calorieapp.presentation.pages.DashboardPages.RecentUploadPlaceholder
import com.example.calorieapp.presentation.pages.DashboardPages.TopHeader
import com.example.calorieapp.presentation.viewModel.DashboardViewModel

@SuppressLint("NewApi")
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {

    val goals by viewModel.dailyGoals.collectAsStateWithLifecycle()
    val summary by viewModel.dailySummary.collectAsStateWithLifecycle()

    val targetCals = goals?.calories ?: 0
    val targetProtein = goals?.protein ?: 0
    val targetCarbs = goals?.carbs ?: 0
    val targetFats = goals?.fats ?: 0

    val consumedCals = summary?.totalCalories ?: 0
    val consumedProtein = summary?.totalProtein ?: 0
    val consumedCarbs = summary?.totalCarbs ?: 0
    val consumedFats = summary?.totalFats ?: 0

    // Left = Target - Consumed (Agar 0 se kam ho jaye toh 0 hi show ho)
    val leftCals = maxOf(0, targetCals - consumedCals)
    val leftProtein = maxOf(0, targetProtein - consumedProtein)
    val leftCarbs = maxOf(0, targetCarbs - consumedCarbs)
    val leftFats = maxOf(0, targetFats - consumedFats)

    // Progress bar ke liye values (0.0 se 1.0 tak)
    val calProgress = if (targetCals > 0) consumedCals.toFloat() / targetCals else 0f
    val proteinProgress = if (targetProtein > 0) consumedProtein.toFloat() / targetProtein else 0f
    val carbsProgress = if (targetCarbs > 0) consumedCarbs.toFloat() / targetCarbs else 0f
    val fatsProgress = if (targetFats > 0) consumedFats.toFloat() / targetFats else 0f

    // 3. Background Gradient (Image jaisa softly blend hota hua)
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFDF0F3), // Halka pink/peach
            Color(0xFFF4F5FB), // Halka blue
            Color.White,
            Color.White
        )
    )

    // Main UI Layout
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add Meal logic */ },
                containerColor = Color.Black,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Meal", modifier = Modifier.size(32.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = MaterialTheme.colorScheme.background
    ){innerPadding->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)// Gradient applied
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp)) // Status bar ki jagah

            TopHeader()

            Spacer(modifier = Modifier.height(24.dp))

            DateSelectorRow()

            Spacer(modifier = Modifier.height(24.dp))

            CaloriesCard(leftCals, calProgress)

            Spacer(modifier = Modifier.height(16.dp))

            MacrosRow(
                leftProtein, proteinProgress,
                leftCarbs, carbsProgress,
                leftFats, fatsProgress
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Recently uploaded",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            RecentUploadPlaceholder()
        }

    }
}