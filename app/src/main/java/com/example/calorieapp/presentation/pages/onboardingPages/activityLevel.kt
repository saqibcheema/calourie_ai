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
fun ActivityLevel(
    selectedActivity: String,
    onActivitySelected: (String) -> Unit,
    onContinue: () -> Unit
){
    var activityList = listOf<String>("No Exercise","Low Activity","Moderate Activity","High Activity")
    var daysForActivityList = listOf<String?>(null,"1-3 days","3-5 days","5-7 days")
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible.value = true }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ){
        Text(
            text ="Physical Activity",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text ="This helps us calculate your daily calorie needs accurately.",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 17.sp,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        
        activityList.forEachIndexed { index, it ->
            AnimatedVisibility(
                visible = visible.value,
                enter = fadeIn(tween(delayMillis = index * 100)) + 
                        slideInVertically(tween(delayMillis = index * 100)) { 20 }
            ) {
                CustomOptionButton(
                    text = it,
                    subText = daysForActivityList[index],
                    isSelected = it == selectedActivity,
                    onClick = {
                        onActivitySelected(it)
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
