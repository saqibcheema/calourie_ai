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
import com.example.calorieapp.domain.useCases.CalculationUtils
import com.example.calorieapp.presentation.components.ContinueButton
import com.example.calorieapp.presentation.components.CustomOptionButton

/**
 * Pregnancy / Breastfeeding Screen.
 * Shown ONLY when gender = Female.
 * Prevents accidental "Lose Weight" goals during pregnancy.
 */
@Composable
fun PregnancyScreen(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit,
    onContinue: () -> Unit
) {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible.value = true }

    val options = listOf(
        Pair(CalculationUtils.PREGNANCY_NONE,          "Not pregnant"),
        Pair(CalculationUtils.PREGNANCY_FIRST,         "Pregnant — 1st Trimester"),
        Pair(CalculationUtils.PREGNANCY_SECOND,        "Pregnant — 2nd Trimester  (+340 kcal)"),
        Pair(CalculationUtils.PREGNANCY_THIRD,         "Pregnant — 3rd Trimester  (+450 kcal)"),
        Pair(CalculationUtils.PREGNANCY_BREASTFEEDING, "Breastfeeding  (+500 kcal)"),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Are you pregnant or\nbreastfeeding?",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Your calorie needs are significantly higher during this period.",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
            Spacer(Modifier.height(32.dp))

            options.forEachIndexed { index, (key, label) ->
                AnimatedVisibility(
                    visible = visible.value,
                    enter = fadeIn(tween(delayMillis = index * 100)) +
                            slideInVertically(tween(delayMillis = index * 100)) { 20 }
                ) {
                    CustomOptionButton(
                        text = label,
                        isSelected = key == selectedStatus,
                        onClick = { onStatusSelected(key) }
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
