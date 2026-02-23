package com.example.calorieapp.presentation.pages.onBoradingPages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    var activityLevelList = listOf<String>("Lose Weight","Maintain","Gain Weight")
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
        Spacer(Modifier.height(5.dp))
        Text(
                text ="This helps us generate a plan for your calorie intake.",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 18.sp,
        )
        Spacer(modifier = Modifier.weight(1f))
        activityLevelList.forEach {
            CustomOptionButton(
                text = it,
                isSelected = it == selectedGoal,
                onClick = {
                    onGoalSelected(it)
                }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        ContinueButton(
            onContinue = onContinue
        )
        Spacer(modifier = Modifier.height(30.dp))
    }
}