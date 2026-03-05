package com.example.calorieapp.presentation.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush) // Gradient applied
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






@Composable
fun CaloriesCard(leftCals: Int, progress: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text Section
            Column {
                Text(
                    text = leftCals.toString(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
                Text(
                    text = "Calories left",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            // Circular Progress Section
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = {
                        1f // Track background
                    },
                    modifier = Modifier.size(100.dp),
                    color = Color(0xFFF0F0F0),
                    strokeWidth = 12.dp,
                    trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                )
                CircularProgressIndicator(
                    progress = {
                        progress // Actual progress
                    },
                    modifier = Modifier.size(100.dp),
                    color = Color.Black,
                    strokeWidth = 12.dp,
                    trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                    strokeCap = StrokeCap.Round,
                )
                // Center Fire Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFF5F5F5), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Calories",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}



