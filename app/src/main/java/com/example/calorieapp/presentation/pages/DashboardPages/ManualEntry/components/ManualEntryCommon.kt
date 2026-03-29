package com.example.calorieapp.presentation.pages.DashboardPages.ManualEntry.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.BrunchDining
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.example.calorieapp.data.DataSource.remote.dto.ClarificationQuestion
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorieapp.ui.theme.*
import kotlinx.coroutines.delay

// ── Meal Type Chip Row (Breakfast / Lunch / Dinner / Snack) ─────────────────

data class MealTypeOption(
    val label: String,
    val icon: ImageVector
)

val mealTypeOptions = listOf(
    MealTypeOption("Breakfast", Icons.Default.FreeBreakfast),
    MealTypeOption("Lunch", Icons.Default.LunchDining),
    MealTypeOption("Dinner", Icons.Default.DinnerDining),
    MealTypeOption("Snack", Icons.Default.Icecream)
)

@Composable
fun MealTypeChipRow(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        mealTypeOptions.forEach { option ->
            val isSelected = selected == option.label

            val containerColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                animationSpec = tween(200, easing = FastOutSlowInEasing),
                label = "mealTypeColor"
            )
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                animationSpec = tween(200, easing = FastOutSlowInEasing),
                label = "mealTypeContentColor"
            )

            Surface(
                onClick = { onSelect(option.label) },
                color = containerColor,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
                shadowElevation = if (isSelected) 2.dp else 0.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = option.label,
                        tint = contentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = option.label,
                        color = contentColor,
                        style = AppTypography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Medium),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ── Context Chip (Restaurant / Home Cooked / Street Food) ───────────────────

data class ContextOption(
    val label: String,
    val icon: ImageVector
)

val contextOptions = listOf(
    ContextOption("Restaurant", Icons.Default.Restaurant),
    ContextOption("Home Cooked", Icons.Default.Home),
    ContextOption("Street Food", Icons.Default.Storefront)
)

@Composable
fun ContextChipRow(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        contextOptions.forEach { option ->
            val isSelected = selected == option.label

            val containerColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.White,
                animationSpec = tween(200, easing = FastOutSlowInEasing),
                label = "contextColor"
            )
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else SlateGrey,
                animationSpec = tween(200, easing = FastOutSlowInEasing),
                label = "contextContentColor"
            )

            Surface(
                onClick = { onSelect(option.label) },
                color = containerColor,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(34.dp),
                border = if (!isSelected) BorderStroke(1.dp, DisabledGrey) else null
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = option.label,
                        color = contentColor,
                        style = AppTypography.labelSmall,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ── Accuracy Nudge Banner ───────────────────────────────────────────────────

@Composable
fun AccuracyNudgeBanner(
    description: String,
    modifier: Modifier = Modifier
) {
    // Show if description text has no numbers (no quantities mentioned)
    val hasQuantity = description.any { it.isDigit() }
    val isLongEnough = description.trim().length >= 3

    AnimatedVisibility(
        visible = isLongEnough && !hasQuantity,
        enter = fadeIn(tween(300)) + expandVertically(tween(300)),
        exit = fadeOut(tween(200)) + shrinkVertically(tween(200)),
        modifier = modifier
    ) {
        ElevatedCard(
            colors = CardDefaults.elevatedCardColors(
                containerColor = CarbsOrange.copy(alpha = 0.12f)
            ),
            elevation = CardDefaults.elevatedCardElevation(0.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = CalorieOrange,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "💡 Add quantities for better accuracy (e.g. \"2 roti\", \"1 cup\")",
                    style = AppTypography.labelSmall.copy(color = CharcoalBlack),
                    maxLines = 2
                )
            }
        }
    }
}

// ── Rotating Placeholder ────────────────────────────────────────────────────

private val placeholderExamples = listOf(
    "e.g. 2 roti with 2 fried eggs and 1 cup tea",
    "e.g. 2 slice pizza and a glass of coke",
    "e.g. chicken biryani medium plate from restaurant",
    "e.g. 1 zinger burger, fries, and a drink",
    "e.g. dal chawal with 1 roti",
    "e.g. 2 paratha with omelette and chai",
    "e.g. grilled chicken breast 200g with salad",
    "e.g. nihari with 2 naan",
    "e.g. bowl of fruit chaat with chutney"
)

@Composable
fun rememberRotatingPlaceholder(): String {
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            currentIndex = (currentIndex + 1) % placeholderExamples.size
        }
    }

    return placeholderExamples[currentIndex]
}

// ── SectionCard (retained, still used for results) ──────────────────────────

@Composable
fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            content()
        }
    }
}

// ── Clarification UI ──────────────────────────────────────────────────────────
@Composable
fun ClarificationForm(
    questions: List<ClarificationQuestion>,
    answers: Map<String, String>,
    onAnswerChanged: (String, String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.elevatedCardElevation(0.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Answers Needed",
                        style = AppTypography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                TextButton(
                    onClick = onCancel,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Text("Cancel", style = AppTypography.labelMedium)
                }
            }

            // Questions List
            questions.forEach { q ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = q.question,
                        style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    val currentAnswer = answers[q.question] ?: ""
                    
                    if (q.options.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            q.options.forEach { option ->
                                val isSelected = currentAnswer == option
                                Surface(
                                    onClick = { onAnswerChanged(q.question, option) },
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = option,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                            style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Medium)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    OutlinedTextField(
                        value = if (q.options.contains(currentAnswer)) "" else currentAnswer,
                        onValueChange = { onAnswerChanged(q.question, it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp),
                        placeholder = {
                            Text(
                                text = "Or type your own...",
                                style = AppTypography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        textStyle = AppTypography.bodyMedium
                    )
                }
            }
            
            // Submit Button
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = true
            ) {
                Text("Confirm Details", style = AppTypography.labelLarge)
            }
        }
    }
}

// ── Custom FlowRow wrapper ──────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        content = { content() }
    )
}
