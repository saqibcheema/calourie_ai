package com.example.calorieapp.presentation.viewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorieapp.domain.entities.DailyMacrosSummary
import com.example.calorieapp.domain.entities.DailyGoals
import com.example.calorieapp.domain.repository.BarcodeRepository
import com.example.calorieapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val barcodeRepository: BarcodeRepository
) : ViewModel() {

    // Goals
    val dailyGoals = userRepository.getGoals()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Monthly Calorie Data for Heatmap
    private val _monthlyCalories = MutableStateFlow<Map<String, Double>>(emptyMap())
    val monthlyCalories = _monthlyCalories.asStateFlow()

    // Monthly Averages for Goal Consistency
    private val _averageMonthlySummary = MutableStateFlow<DailyMacrosSummary?>(null)
    val averageMonthlySummary = _averageMonthlySummary.asStateFlow()

    // Macro Summary for Today (Used if needed elsewhere)
    private val _todaySummary = MutableStateFlow<DailyMacrosSummary?>(null)
    val todaySummary = _todaySummary.asStateFlow()

    init {
        loadMonthlyData()
        loadTodaySummary()
    }

    private fun loadMonthlyData() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val monthFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val currentMonthPattern = "${monthFormat.format(calendar.time)}-%"

            barcodeRepository.getMonthlyMacros(currentMonthPattern).collectLatest { summaries ->
                // Map to date/calories for the heatmap
                val calorieMap = summaries.associate { it.dateString to (it.totalCalories ?: 0.0) }
                _monthlyCalories.value = calorieMap

                // Calculate averages for days that have logs
                if (summaries.isNotEmpty()) {
                    var sumCarbs = 0.0
                    var sumProtein = 0.0
                    var sumFats = 0.0
                    var activeDays = 0

                    summaries.forEach { summary ->
                        if ((summary.totalCalories ?: 0.0) > 0) {
                            sumCarbs += summary.totalCarbs ?: 0.0
                            sumProtein += summary.totalProtein ?: 0.0
                            sumFats += summary.totalFats ?: 0.0
                            activeDays++
                        }
                    }

                    if (activeDays > 0) {
                        _averageMonthlySummary.value = DailyMacrosSummary(
                            totalCalories = null, // don't need for macro section
                            totalCarbs = sumCarbs / activeDays,
                            totalProtein = sumProtein / activeDays,
                            totalFats = sumFats / activeDays
                        )
                    } else {
                        _averageMonthlySummary.value = null
                    }
                } else {
                    _averageMonthlySummary.value = null
                }
            }
        }
    }

    private fun loadTodaySummary() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            barcodeRepository.getDailySummary(today).collectLatest {
                _todaySummary.value = it
            }
        }
    }
}
