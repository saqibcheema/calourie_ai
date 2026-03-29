package com.example.calorieapp.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorieapp.domain.entities.Product
import com.example.calorieapp.ui.theme.*

@Composable
fun NutritionSummary(
    product: Product,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Calories Hero Card
        StaggeredEntry(delayMs = 100) {
            CaloriesHeroCard(calories = product.calories)
        }

        // Macros Header
        StaggeredEntry(delayMs = 200) {
            Text(
                text = "MACRO BREAKDOWN",
                style = MaterialTheme.typography.labelLarge.copy(
                    letterSpacing = 1.5.sp,
                    color = SlateGrey
                ),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        // Macros Grid
        StaggeredEntry(delayMs = 300) {
            MacrosGrid(product = product)
        }
    }
}

@Composable
private fun CaloriesHeroCard(calories: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(vertical = 32.dp, horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TOTAL CALORIES",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.White.copy(alpha = 0.8f),
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${calories.toInt()}",
                        style = MaterialTheme.typography.displayLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "kcal",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MacrosGrid(product: Product) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MacroItemCard(
                modifier = Modifier.weight(1f),
                label = "PROTEIN",
                value = "${product.protein.toInt()}g",
                color = ProteinRed,
                progress = (product.protein / 50.0).coerceIn(0.0, 1.0).toFloat()
            )
            MacroItemCard(
                modifier = Modifier.weight(1f),
                label = "CARBS",
                value = "${product.carbs.toInt()}g",
                color = CarbsOrange,
                progress = (product.carbs / 250.0).coerceIn(0.0, 1.0).toFloat()
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MacroItemCard(
                modifier = Modifier.weight(1f),
                label = "FAT",
                value = "${product.fat.toInt()}g",
                color = FatsBlue,
                progress = (product.fat / 70.0).coerceIn(0.0, 1.0).toFloat()
            )
            MacroItemCard(
                modifier = Modifier.weight(1f),
                label = "FIBER",
                value = "${(product.fiber ?: 0.0).toInt()}g",
                color = SuccessGreen,
                progress = ((product.fiber ?: 0.0) / 25.0).coerceIn(0.0, 1.0).toFloat()
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MacroItemCard(
                modifier = Modifier.weight(1f),
                label = "SUGARS",
                value = "${(product.sugars ?: 0.0).toInt()}g",
                color = CalorieOrange,
                progress = ((product.sugars ?: 0.0) / 50.0).coerceIn(0.0, 1.0).toFloat()
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun MacroItemCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color,
    progress: Float
) {
    var animFraction by remember { mutableStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = animFraction,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "macroProgress"
    )
    LaunchedEffect(Unit) { animFraction = progress }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = GhostWhite,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = color.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = CharcoalBlack,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Custom Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(DisabledGrey, RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(6.dp)
                        .background(color, RoundedCornerShape(3.dp))
                )
            }
        }
    }
}

@Composable
fun StaggeredEntry(
    delayMs: Int,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delayMs.toLong())
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500)) + 
                slideInVertically(
                    initialOffsetY = { 40 }, 
                    animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow)
                )
    ) {
        content()
    }
}
