package com.example.calorieapp.presentation.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calorieapp.presentation.viewModel.DashboardViewModel

@Composable
fun DashBoardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
){
    val goals by viewModel.dailyGoals.collectAsStateWithLifecycle()

    if(goals == null){
        CircularProgressIndicator()
    }else{
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "My Target Calories: ${goals!!.calories} kcal")
            Text(text = "Protein Goal: ${goals!!.protein} g")
            Text(text = "Fats Goal: ${goals!!.fats} g")
            Text(text = "Carbs Goal: ${goals!!.carbs} g")
        }
    }
}