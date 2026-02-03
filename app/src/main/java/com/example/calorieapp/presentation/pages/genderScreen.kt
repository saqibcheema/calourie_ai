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
fun GenderScreen(
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    onContinue: () -> Unit
){
    var gender = listOf<String>("Male","Female","Other")
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
        gender.forEach {
            CustomOptionButton(
                text = it,
                isSelected = it == selectedGender,
                onClick = {
                    onGenderSelected(it)
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
