package com.example.calorieapp.presentation.pages.onboardingPages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorieapp.presentation.components.ContinueButton
import com.example.calorieapp.presentation.components.CustomOptionButton

/**
 * Goal Pace Screen — lets user choose how aggressively they want to hit their goal.
 * MyFitnessPal / Lose It! style pace selection.
 * Only shown when goal is "Lose Weight" or "Gain Weight".
 */
@Composable
fun GoalPaceScreen(
    selectedPace: String,
    goal: String,
    onPaceSelected: (String) -> Unit,
    onContinue: () -> Unit
) {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible.value = true }

    val isLosing = goal == "Lose Weight"

    val paceOptions = listOf(
        Triple(
            "Slow",
            if (isLosing) "~0.25 kg / week" else "~0.25 kg / week",
            if (isLosing) "Gentle cut — easier to sustain" else "Lean bulk — minimal fat gain"
        ),
        Triple(
            "Moderate",
            if (isLosing) "~0.5 kg / week" else "~0.5 kg / week",
            if (isLosing) "Recommended by most dietitians" else "Best muscle-to-fat ratio"
        ),
        Triple(
            "Fast",
            if (isLosing) "~0.75 kg / week" else "~0.75 kg / week",
            if (isLosing) "Aggressive — needs strong commitment" else "Faster gains, some fat expected"
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (isLosing) "How fast do you want to lose?" else "How fast do you want to gain?",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "A slower pace is easier to maintain and healthier long-term.",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
            Spacer(Modifier.height(32.dp))

            paceOptions.forEachIndexed { index, (pace, rate, description) ->
                AnimatedVisibility(
                    visible = visible.value,
                    enter = fadeIn(tween(delayMillis = index * 120)) +
                            slideInVertically(tween(delayMillis = index * 120)) { 20 }
                ) {
                    CustomOptionButton(
                        text = pace,
                        subText = "$rate  •  $description",
                        isSelected = pace == selectedPace,
                        onClick = { onPaceSelected(pace) }
                    )
                }
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
