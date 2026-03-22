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
import com.example.calorieapp.domain.useCases.UpdateMealQuantityUseCase
import com.example.calorieapp.domain.useCases.DeleteMealUseCase
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getGoalsUseCase: GetGoalsUseCase,
    private val getDailySummaryUseCase: GetTodayNutrimentsSummaryUseCase,
    private val getMealsByDateUseCase: GetMealsByDateUseCase,
    private val updateMealQuantityUseCase: UpdateMealQuantityUseCase,
    private val deleteMealUseCase: DeleteMealUseCase
) : ViewModel() {
    var dailyGoals : StateFlow<DailyGoals?> = getGoalsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    @SuppressLint("NewApi")
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    @SuppressLint("NewApi")
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    @SuppressLint("NewApi")
    var dailySummary : StateFlow<DailyMacrosSummary?> = _selectedDate
        .flatMapLatest { date -> getDailySummaryUseCase(date.toString()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    @SuppressLint("NewApi")
    var dailyMeals : StateFlow<List<Product>> = _selectedDate
        .flatMapLatest { date -> getMealsByDateUseCase(date.toString()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun increaseQuantity(product: Product) {
        viewModelScope.launch {
            updateMealQuantityUseCase(product.barcode, product.quantity + 1)
        }
    }

    fun decreaseQuantityOrDelete(product: Product) {
        viewModelScope.launch {
            if (product.quantity > 1) {
                updateMealQuantityUseCase(product.barcode, product.quantity - 1)
            } else {
                deleteMealUseCase(product.barcode)
            }
        }
    }
}