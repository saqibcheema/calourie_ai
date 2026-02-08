package com.example.calorieapp.presentation.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.calorieapp.presentation.viewModel.OnBoardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dashBoardScreen(viewModel: OnBoardingViewModel = hiltViewModel()){
    Scaffold (
        topBar ={
            TopAppBar(
                title = {
                    Text(text = "Dashboard")
                }
            )
        }
    ){ paddingValues->
        Box(
            modifier = Modifier.padding(paddingValues)
        ){
            Text("viewModel.")
        }
    }
}