package com.example.calorieapp.presentation.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calorieapp.presentation.pages.DashboardPages.CaloriesCard
import com.example.calorieapp.presentation.pages.DashboardPages.DateSelectorRow
import com.example.calorieapp.presentation.pages.DashboardPages.MacrosRow
import com.example.calorieapp.presentation.pages.DashboardPages.RecentUploadPlaceholder
import com.example.calorieapp.presentation.pages.DashboardPages.TopHeader
import com.example.calorieapp.presentation.viewModel.DashboardViewModel
import com.example.calorieapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("NewApi")
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {

    val goals by viewModel.dailyGoals.collectAsStateWithLifecycle()
    val summary by viewModel.dailySummary.collectAsStateWithLifecycle()
    var showNutritionSheet by remember { mutableStateOf(false) }

    val targetCals = goals?.calories ?: 0
    val targetProtein = goals?.protein ?: 0
    val targetCarbs = goals?.carbs ?: 0
    val targetFats = goals?.fats ?: 0

    val consumedCals = summary?.totalCalories ?: 0
    val consumedProtein = summary?.totalProtein ?: 0
    val consumedCarbs = summary?.totalCarbs ?: 0
    val consumedFats = summary?.totalFats ?: 0

    // Left = Target - Consumed (Agar 0 se kam ho jaye toh 0 hi show ho)
    val leftCals = maxOf(0, targetCals - consumedCals.toInt())
    val leftProtein = maxOf(0, targetProtein - consumedProtein.toInt())
    val leftCarbs = maxOf(0, targetCarbs - consumedCarbs.toInt())
    val leftFats = maxOf(0, targetFats - consumedFats.toInt())

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
                onClick = { showNutritionSheet = true },
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
    if (showNutritionSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = {
                showNutritionSheet = false
            },
            sheetState = sheetState,
            containerColor = PureWhite,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            NutritionSheetContent(
                onDismiss = { showNutritionSheet = false }
            )
        }
    }
}
@Composable
fun NutritionSheetContent(onDismiss: () -> Unit) {
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
            onClick = { /* Scan Barcode ki logic yahan aayegi */ }
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
            onClick = { /* Manual Entry ki logic yahan aayegi */ }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun LogOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = GhostWhite,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DisabledGrey),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = CharcoalBlack
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = AppTypography.titleMedium.copy(
                            color = CharcoalBlack,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    if (badgeText != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = DisabledGrey,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = badgeText,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = AppTypography.labelSmall.copy(color = CharcoalBlack)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = AppTypography.bodyMedium.copy(color = SlateGrey)
                )
            }

            // Arrow Icon
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Proceed",
                tint = SlateGrey
            )
        }
    }
}