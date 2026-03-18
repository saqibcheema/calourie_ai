package com.example.calorieapp.presentation.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorieapp.domain.entities.DailyGoals
import com.example.calorieapp.domain.entities.DailyMacrosSummary
import com.example.calorieapp.domain.entities.Product
import com.example.calorieapp.domain.useCases.GetGoalsUseCase
import com.example.calorieapp.domain.useCases.GetMealsByDateUseCase
import com.example.calorieapp.domain.useCases.GetTodayNutrimentsSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getGoalsUseCase: GetGoalsUseCase,
    private val getDailySummaryUseCase: GetTodayNutrimentsSummaryUseCase,
    private val getMealsByDateUseCase: GetMealsByDateUseCase
) : ViewModel() {
    var dailyGoals : StateFlow<DailyGoals?> = getGoalsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    @SuppressLint("NewApi")
    val selectedDate = LocalDate.now().toString()

    var dailySummary : StateFlow<DailyMacrosSummary?> = getDailySummaryUseCase(selectedDate)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    var dailyMeals : StateFlow<List<Product>> = getMealsByDateUseCase(selectedDate)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}