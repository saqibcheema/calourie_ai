package com.example.calorieapp.presentation.pages.DashboardPages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.calorieapp.ui.theme.ProteinRed
import com.example.calorieapp.ui.theme.CarbsOrange
import com.example.calorieapp.ui.theme.FatsBlue

@Composable
fun MacrosRow(
    leftProtein: Int, proteinProgress: Float,
    leftCarbs: Int, carbsProgress: Float,
    leftFats: Int, fatsProgress: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MacroCard(
            modifier = Modifier.weight(1f),
            value = "${leftProtein}g",
            title = "Protein left",
            progress = proteinProgress,
            icon = Icons.Default.SetMeal, // Default Fish/Meat icon
            iconColor = ProteinRed // Reddish
        )
        MacroCard(
            modifier = Modifier.weight(1f),
            value = "${leftCarbs}g",
            title = "Carbs left",
            progress = carbsProgress,
            icon = Icons.Default.BreakfastDining, // Default Bread/Carbs icon
            iconColor = CarbsOrange // Orange
        )
        MacroCard(
            modifier = Modifier.weight(1f),
            value = "${leftFats}g",
            title = "Fats left",
            progress = fatsProgress,
            icon = Icons.Default.WaterDrop, // Default Oil/Fat icon
            iconColor = FatsBlue // Blue
        )
    }
}

