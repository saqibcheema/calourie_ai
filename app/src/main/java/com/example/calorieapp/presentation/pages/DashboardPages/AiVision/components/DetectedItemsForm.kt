package com.example.calorieapp.presentation.pages.DashboardPages.AiVision.components

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorieapp.data.DataSource.remote.dto.DetectedFoodItem
import com.example.calorieapp.presentation.pages.DashboardPages.ManualEntry.components.ContextChipRow
import com.example.calorieapp.presentation.pages.DashboardPages.ManualEntry.components.MealTypeChipRow
import com.example.calorieapp.ui.theme.*

@Composable
fun DetectedItemsForm(
    capturedBitmap: Bitmap?,
    detectedItems: List<DetectedFoodItem>,
    itemPortions: Map<String, String>,
    mealType: String,
    eatingContext: String,
    isClarificationNeeded: Boolean,
    clarificationQuestions: List<com.example.calorieapp.data.DataSource.remote.dto.ClarificationQuestion>,
    clarificationAnswers: Map<String, String>,
    errorMessage: String?,
    onMealTypeChange: (String) -> Unit,
    onEatingContextChange: (String) -> Unit,
    onPortionChanged: (String, String) -> Unit,
    onRemoveItem: (String) -> Unit,
    onAddItem: (String) -> Unit,
    onSubmit: () -> Unit,
    onClarificationAnswerChanged: (String, String) -> Unit,
    onSubmitClarifications: () -> Unit,
    onCancelClarification: () -> Unit
) {
    val scrollState = rememberScrollState()
    var newItemText by remember { mutableStateOf("") }
    var showAddItemField by remember { mutableStateOf(false) }

    val canSubmit = detectedItems.isNotEmpty() &&
            detectedItems.any { (itemPortions[it.name] ?: "").isNotBlank() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .imePadding()
            .navigationBarsPadding()
    ) {
        // ── Header with photo thumbnail ───────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            if (capturedBitmap != null) {
                Image(
                    bitmap = capturedBitmap.asImageBitmap(),
                    contentDescription = "Captured meal",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = SlateGrey
                    )
                }
            }

            // Gradient overlay on bottom of image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )

            // Gemini detected badge
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.92f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Gemini detected ${detectedItems.size} item${if (detectedItems.size != 1) "s" else ""}",
                        style = AppTypography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Instruction ──────────────────────────────────────────────────
            Text(
                text = "Add portion sizes",
                style = AppTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = CharcoalBlack
            )
            Text(
                text = "Tell us how much of each item you ate for accurate calories",
                style = AppTypography.bodyMedium,
                color = SlateGrey
            )

            // ── Meal Type ────────────────────────────────────────────────────
            Text(
                text = "What meal is this?",
                style = AppTypography.labelLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            MealTypeChipRow(
                selected = mealType,
                onSelect = onMealTypeChange
            )

            // ── Detected Items Cards ─────────────────────────────────────────
            detectedItems.forEach { item ->
                DetectedItemCard(
                    item = item,
                    portion = itemPortions[item.name] ?: "",
                    onPortionChanged = { onPortionChanged(item.name, it) },
                    onRemove = { onRemoveItem(item.name) },
                    canRemove = detectedItems.size > 1
                )
            }

            // ── Add Item ──────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = showAddItemField,
                enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GhostWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newItemText,
                            onValueChange = { newItemText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = {
                                Text("Add missing item...", style = AppTypography.bodySmall)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Done
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = DisabledGrey
                            )
                        )
                        IconButton(
                            onClick = {
                                if (newItemText.isNotBlank()) {
                                    onAddItem(newItemText)
                                    newItemText = ""
                                    showAddItemField = false
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            TextButton(
                onClick = { showAddItemField = !showAddItemField },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (showAddItemField) "Cancel" else "Add missing item",
                    style = AppTypography.labelMedium
                )
            }

            // ── Clarification form (reuse pattern) ───────────────────────────
            AnimatedVisibility(
                visible = isClarificationNeeded,
                enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
            ) {
                com.example.calorieapp.presentation.pages.DashboardPages.ManualEntry.components.ClarificationForm(
                    questions = clarificationQuestions,
                    answers = clarificationAnswers,
                    onAnswerChanged = onClarificationAnswerChanged,
                    onSubmit = onSubmitClarifications,
                    onCancel = onCancelClarification
                )
            }

            // ── Eating Context ────────────────────────────────────────────────
            Text(
                text = "Where did you eat? (optional)",
                style = AppTypography.labelMedium,
                color = SlateGrey
            )
            ContextChipRow(
                selected = eatingContext,
                onSelect = onEatingContextChange
            )

            // ── Error message ─────────────────────────────────────────────────
            AnimatedVisibility(visible = errorMessage != null) {
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = AppTypography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Submit button ─────────────────────────────────────────────────
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = canSubmit && !isClarificationNeeded,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = DisabledGrey
                )
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Calculate Nutrition",
                    style = AppTypography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetectedItemCard(
    item: DetectedFoodItem,
    portion: String,
    onPortionChanged: (String) -> Unit,
    onRemove: () -> Unit,
    canRemove: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Food emoji indicator
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "🍽", fontSize = 18.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = item.name,
                            style = AppTypography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = CharcoalBlack
                        )
                        if (item.estimatedPortion != null) {
                            Text(
                                text = "Gemini estimate: ${item.estimatedPortion}",
                                style = AppTypography.bodySmall,
                                color = SlateGrey
                            )
                        }
                    }
                }

                if (canRemove) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove ${item.name}",
                            tint = SlateGrey,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = portion,
                onValueChange = onPortionChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = item.estimatedPortion?.let { "Gemini suggests: $it" }
                            ?: "e.g. 1 plate, 2 pieces, 200g...",
                        style = AppTypography.bodySmall,
                        color = SlateGrey.copy(alpha = 0.65f)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = DisabledGrey,
                    focusedContainerColor = GhostWhite.copy(alpha = 0.5f),
                    unfocusedContainerColor = GhostWhite.copy(alpha = 0.3f)
                ),
                textStyle = AppTypography.bodyMedium,
                label = {
                    Text("Portion size", style = AppTypography.labelSmall)
                }
            )
        }
    }
}
