package com.example.calorieapp.domain.useCases

import com.example.calorieapp.domain.entities.DailyMacrosSummary
import com.example.calorieapp.domain.repository.BarcodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayNutrimentsSummaryUseCase @Inject constructor(
    private val repository: BarcodeRepository
) {
    operator fun invoke(selectedDate: String): Flow<DailyMacrosSummary?> {
        return repository.getDailySummary(selectedDate)
    }
}