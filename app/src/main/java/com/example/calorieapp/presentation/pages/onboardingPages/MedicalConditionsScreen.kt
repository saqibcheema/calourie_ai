package com.example.calorieapp.presentation.pages.onboardingPages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorieapp.domain.useCases.CalculationUtils
import com.example.calorieapp.presentation.components.ContinueButton

/**
 * Medical Conditions Screen.
 * Allows multi-select — user can have multiple conditions.
 * "None" deselects everything else.
 * Noom-style — framed as caring about health, not intrusive.
 */
@Composable
fun MedicalConditionsScreen(
    selectedConditions: List<String>,
    onConditionsChanged: (List<String>) -> Unit,
    onContinue: () -> Unit
) {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible.value = true }

    val conditionOptions = listOf(
        Pair(CalculationUtils.CONDITION_DIABETES,  "Diabetes (Type 2)"),
        Pair(CalculationUtils.CONDITION_THYROID,   "Thyroid Condition"),
        Pair(CalculationUtils.CONDITION_PCOS,      "PCOS"),
        Pair(CalculationUtils.CONDITION_KIDNEY,    "Kidney Disease"),
        Pair(CalculationUtils.CONDITION_HEART,     "Heart Condition"),
    )

    val noneLabel = "None of the above"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Any health conditions?",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "This helps us personalize your macros safely. Your data stays private.",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
            Spacer(Modifier.height(32.dp))

            // Condition checkboxes
            conditionOptions.forEachIndexed { index, (key, label) ->
                val isSelected = selectedConditions.contains(key)
                AnimatedVisibility(
                    visible = visible.value,
                    enter = fadeIn(tween(delayMillis = index * 80)) +
                            slideInVertically(tween(delayMillis = index * 80)) { 20 }
                ) {
                    MedicalConditionItem(
                        label = label,
                        isSelected = isSelected,
                        onClick = {
                            val updated = if (isSelected) {
                                selectedConditions - key
                            } else {
                                selectedConditions + key
                            }
                            onConditionsChanged(updated)
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Spacer(Modifier.height(8.dp))

            // "None" option — clears all selections
            AnimatedVisibility(
                visible = visible.value,
                enter = fadeIn(tween(delayMillis = conditionOptions.size * 80)) +
                        slideInVertically(tween(delayMillis = conditionOptions.size * 80)) { 20 }
            ) {
                MedicalConditionItem(
                    label = noneLabel,
                    isSelected = selectedConditions.isEmpty(),
                    onClick = { onConditionsChanged(emptyList()) }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 30.dp)
        ) {
            ContinueButton(onContinue = onContinue)
        }
    }
}

@Composable
private fun MedicalConditionItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

    val bgColor = if (isSelected)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    else
        MaterialTheme.colorScheme.surface

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
