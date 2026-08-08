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

import com.example.calorieapp.domain.useCases.RemainingLimits

@Composable
fun NutritionSheetContent(
    remainingLimits: RemainingLimits?,
    onScanClick: () -> Unit,
    onManualEntryClick: () -> Unit = {},
    onAiVisionClick: () -> Unit = {}
) {
    val scanBadge = if (remainingLimits != null && remainingLimits.remainingBarcodeScans < 999) {
        "${remainingLimits.remainingBarcodeScans} Left"
    } else null

    val manualBadge = if (remainingLimits != null && remainingLimits.remainingManualEntries < 999) {
        "${remainingLimits.remainingManualEntries} Left"
    } else null

    val aiBadge = if (remainingLimits != null && remainingLimits.remainingAiVision < 999) {
        if (remainingLimits.isAiVisionUnlocked) "${remainingLimits.remainingAiVision} Left" else "Locked"
    } else "AI VISION"

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
            badgeText = scanBadge,
            onClick = onScanClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        LogOptionCard(
            title = "Snap a Meal",
            subtitle = "Auto-detect using AI",
            icon = Icons.Default.CameraAlt,
            badgeText = aiBadge,
            onClick = onAiVisionClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        LogOptionCard(
            title = "Manual Entry",
            subtitle = "Describe what you ate in plain text",
            icon = Icons.Default.Edit,
            badgeText = manualBadge,
            onClick = onManualEntryClick
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}