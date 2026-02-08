package com.example.calorieapp.presentation.pages

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
fun ActivityLevel(
    selectedActivity: String,
    onActivitySelected: (String) -> Unit,
    onContinue: () -> Unit
){
    var activityList = listOf<String>("No Exercise","Low Activity","Moderate Activity","High Activity")
    var daysForActivityList = listOf<String?>(null,"1-3 days","3-5 days","5-7 days")
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ){
        Text(
            text ="Choose your Gender",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text ="This will be used to calibrate your custom plan",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 18.sp,
        )
        Spacer(modifier = Modifier.weight(1f))
        activityList.forEach {
            CustomOptionButton(
                text = it,
                subText = daysForActivityList[activityList.indexOf(it)],
                isSelected = it == selectedActivity,
                onClick = {
                    onActivitySelected(it)
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
