package com.example.calorieapp.presentation.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.calorieapp.presentation.pages.onBoradingPages.ActivityLevel
import com.example.calorieapp.presentation.pages.onBoradingPages.AgeScreen
import com.example.calorieapp.presentation.pages.onBoradingPages.GenderScreen
import com.example.calorieapp.presentation.pages.onBoradingPages.GoalScreen
import com.example.calorieapp.presentation.pages.onBoradingPages.HeightAndWeight
import com.example.calorieapp.presentation.viewModel.OnBoardingViewModel

@Composable
fun OnBoardingScreen(viewModel: OnBoardingViewModel = hiltViewModel()){

    val targetProgress = (viewModel.currentStep + 1 ).toFloat() / viewModel.totalSteps

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 500),
        label = "ProgressAnimation"
    )

    Scaffold (
        topBar = {
            Column (
                modifier = Modifier.padding(vertical = 50.dp,horizontal = 20.dp)
            ){
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    if(viewModel.currentStep > 0){
                        IconButton(
                            onClick = {
                                viewModel.onBack()
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back"
                            )
                        }
                    }else{
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.background)
                                .size(40.dp)
                        )
                    }
                    if(viewModel.currentStep != 0){
                        Spacer(modifier = Modifier.width(20.dp))
                    }


                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .weight(1f)
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    ){paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ){
            AnimatedContent(
                targetState = viewModel.currentStep,
                label = "OnboardingTransition",
                modifier = Modifier.fillMaxSize(),
            ) {targetStep ->
                when(targetStep){
                    0-> GenderScreen(
                        selectedGender = viewModel.gender,
                        onGenderSelected = {
                            viewModel.gender = it
                        },
                        onContinue = {
                            viewModel.onNext()
                        }
                    )
                    1-> AgeScreen(
                        onAgeSelected = {
                            viewModel.age = it.toInt()
                        },
                        onContinue = {
                            viewModel.onNext()
                        }
                    )
                    2-> HeightAndWeight(
                        onFeetSelected = {
                            viewModel.feetForHeight = it
                        },
                        onInchesSelected = {
                            viewModel.inchesForHeight = it
                        },
                        onWeightSelected = {
                            viewModel.weight = it
                        },
                        onContinue = {
                            viewModel.onNext()
                        }
                    )
                    3-> ActivityLevel(
                        selectedActivity = viewModel.activityLevel,
                        onActivitySelected = {
                            viewModel.activityLevel = it
                        },
                        onContinue = {
                            viewModel.onNext()
                        }
                    )
                    4-> GoalScreen(
                        selectedGoal = viewModel.goal,
                        onGoalSelected = {
                            viewModel.goal = it
                        },
                        onContinue = {
                            viewModel.onNext()
                        },
                    )
                }
            }
        }
    }
}