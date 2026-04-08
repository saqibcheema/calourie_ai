package com.example.calorieapp.presentation.pages.onboardingPages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorieapp.presentation.components.ContinueButton
import com.example.calorieapp.presentation.components.CustomOptionButton

@Composable
fun GoalScreen(
    selectedGoal: String,
    onGoalSelected: (String) -> Unit,
    onContinue: () -> Unit
){
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible.value = true }
    val activityLevelList = listOf("Lose Weight", "Maintain", "Gain Weight")

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ){
        Text(
            text ="What is your goal?",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text ="This helps us generate a plan for your calorie intake.",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 18.sp,
            lineHeight = 24.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        
        activityLevelList.forEachIndexed { index, it ->
            AnimatedVisibility(
                visible = visible.value,
                enter = fadeIn(tween(delayMillis = index * 100)) + 
                        slideInVertically(tween(delayMillis = index * 100)) { 20 }
            ) {
                CustomOptionButton(
                    text = it,
                    isSelected = it == selectedGoal,
                    onClick = {
                        onGoalSelected(it)
                    }
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        ContinueButton(
            onContinue = onContinue
        )
        Spacer(modifier = Modifier.height(30.dp))
    }
}