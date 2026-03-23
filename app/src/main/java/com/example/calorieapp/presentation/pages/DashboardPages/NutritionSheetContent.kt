package com.example.calorieapp.presentation.pages.DashboardPages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calorieapp.ui.theme.AppTypography
import com.example.calorieapp.ui.theme.CharcoalBlack
import com.example.calorieapp.ui.theme.SlateGrey

@Composable
fun NutritionSheetContent(
    onScanClick: () -> Unit,
    onManualEntryClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Log Nutrition",
                style = AppTypography.headlineMedium.copy(color = CharcoalBlack)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose how you'd like to track your meal",
                style = AppTypography.bodyMedium.copy(color = SlateGrey)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        LogOptionCard(
            title = "Scan Barcode",
            subtitle = "Perfect for packaged products",
            icon = Icons.Default.QrCodeScanner,
            onClick = onScanClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        LogOptionCard(
            title = "Snap a Meal",
            subtitle = "Auto-detect using AI",
            icon = Icons.Default.CameraAlt,
            badgeText = "AI VISION",
            onClick = { /* AI Vision ki logic yahan aayegi */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LogOptionCard(
            title = "Manual Entry",
            subtitle = "Describe what you ate in plain text",
            icon = Icons.Default.Edit,
            onClick = onManualEntryClick
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}