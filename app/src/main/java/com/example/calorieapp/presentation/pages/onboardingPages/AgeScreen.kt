package com.example.calorieapp.presentation.pages.onboardingPages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.calorieapp.presentation.components.WheelPicker

@Composable
fun AgeScreen(
    onAgeSelected: (String) -> Unit,
    onContinue: () -> Unit
){
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible.value = true }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ){
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
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
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            AnimatedVisibility(
                visible = visible.value,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { 20 }
            ) {
                WheelPicker(
                    range = 18..100,
                    initialValue = 25,
                    onValueChange = {
                        onAgeSelected(it.toString())
                    },
                )
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 30.dp)
        ) {
            ContinueButton(
                onContinue = onContinue
            )
        }
    }
}