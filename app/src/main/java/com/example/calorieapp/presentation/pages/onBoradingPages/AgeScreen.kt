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
import com.example.calorieapp.presentation.components.WheelPicker

@Composable
fun AgeScreen(
    onAgeSelected: (String) -> Unit,
    onContinue: () -> Unit
){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ){
        Text(
            text ="What is your Age?",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text ="This will be used to calculate your personalized plan",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.weight(1f))
        WheelPicker(
            range = 18..100,
            initialValue = 25,
            onValueChange = {
                onAgeSelected(it.toString())
            },
        )
        Spacer(modifier = Modifier.weight(1f))
        ContinueButton(
            onContinue = onContinue
        )
        Spacer(modifier = Modifier.height(30.dp))
    }
}