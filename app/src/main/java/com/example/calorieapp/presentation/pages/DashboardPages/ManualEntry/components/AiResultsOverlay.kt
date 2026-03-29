package com.example.calorieapp.presentation.pages.DashboardPages.ManualEntry.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorieapp.domain.entities.Product
import com.example.calorieapp.presentation.components.NutritionSummary
import com.example.calorieapp.presentation.components.StaggeredEntry
import com.example.calorieapp.ui.theme.*

// ─── Loading Overlay ────────────────────────────────────────────────────────

@Composable
fun AiLoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PulsingAiIcon()
            AnalyzingText()
        }
    }
}

@Composable
private fun PulsingAiIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    // Outer ring pulse
    val outerScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "outerScale"
    )
    val outerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "outerAlpha"
    )
    // Inner icon rotation
    val iconRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Box(contentAlignment = Alignment.Center) {
        // Outer glow ring
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(outerScale)
                .alpha(outerAlpha)
                .background(
                    MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        )
        // Mid ring
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = CircleShape
                )
        )
        // Core circle with icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI",
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
            )
        }
    }
}

@Composable
private fun AnalyzingText() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val dotsCount by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dotsAnim"
    )
    val dots = ".".repeat(dotsCount.toInt() + 1)

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Analyzing ingredients with AI$dots",
            style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
            textAlign = TextAlign.Center
        )
        Text(
            text = "LLama 3.3 is estimating your meal",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f)),
            textAlign = TextAlign.Center
        )
    }
}

// ─── Results Screen ──────────────────────────────────────────────────────────

@Composable
fun AiResultsScreen(
    foodName: String,
    calories: Double,
    protein: Double,
    carbs: Double,
    fat: Double,
    fiber: Double,
    sugars: Double,
    confidence: String,
    items: List<com.example.calorieapp.data.DataSource.remote.dto.FoodItemEstimate> = emptyList(),
    onDone: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
        ) + fadeIn(animationSpec = tween(300))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Success badge
                SuccessBadge()

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                AnimatedEntry(delayMs = 100) {
                    Text(
                        text = "$foodName Analyzed!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalBlack
                        ),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                AnimatedEntry(delayMs = 180) {
                    Text(
                        text = "AI has estimated the nutritional values.\nYour meal has been saved.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SlateGrey),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Unified Nutrition Summary
                NutritionSummary(
                    product = Product(
                        barcode = "ai_estimate",
                        productName = foodName,
                        brand = "AI Estimated",
                        imageUrl = null,
                        calories = calories,
                        protein = protein,
                        carbs = carbs,
                        fat = fat,
                        fiber = fiber,
                        sugars = sugars
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Itemized Breakdown (if more than 1 item)
                if (items.size > 1) {
                    AnimatedEntry(delayMs = 400) {
                        FoodItemBreakdown(items)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Confidence badge
                if (confidence == "low") {
                    AnimatedEntry(delayMs = 460) {
                        LowConfidenceBanner()
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Done button
                AnimatedEntry(delayMs = 500) {
                    Button(
                        onClick = onDone,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Done", style = MaterialTheme.typography.titleMedium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun LowConfidenceBanner() {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Rough estimate — add more detail when logging for better accuracy",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            )
        }
    }
}

@Composable
private fun SuccessBadge() {
    var scale by remember { mutableStateOf(0f) }
    val animScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "badgeScale"
    )
    LaunchedEffect(Unit) { scale = 1f }

    Box(
        modifier = Modifier.scale(animScale),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow
        Box(
            modifier = Modifier
                .size(110.dp)
                .background(SuccessLightGreen, shape = CircleShape)
        )
        // Inner circle
        Box(
            modifier = Modifier
                .size(76.dp)
                .background(SuccessGreen, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

// ─── Shared animated entry wrapper ──────────────────────────────────────────

@Composable
private fun AnimatedEntry(delayMs: Int = 0, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delayMs.toLong())
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(350)) +
                slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(350, easing = FastOutSlowInEasing)
                )
    ) {
        content()
    }
}

// ── Itemized Breakdown ──────────────────────────────────────────────────────

@Composable
fun FoodItemBreakdown(items: List<com.example.calorieapp.data.DataSource.remote.dto.FoodItemEstimate>) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = GhostWhite),
        elevation = CardDefaults.elevatedCardElevation(0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            // Header row (always visible)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "View Breakdown (${items.size} items)",
                    style = AppTypography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = CharcoalBlack,
                    modifier = Modifier.weight(1f)
                )
            }

            // Expanded list
            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    style = AppTypography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = CharcoalBlack
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "P: ${item.protein.toInt()}g • C: ${item.carbs.toInt()}g • F: ${item.fat.toInt()}g",
                                    style = AppTypography.bodySmall,
                                    color = SlateGrey
                                )
                            }
                            Text(
                                text = "${item.calories.toInt()} kcal",
                                style = AppTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = CalorieOrange
                            )
                        }
                    }
                }
            }
        }
    }
}
