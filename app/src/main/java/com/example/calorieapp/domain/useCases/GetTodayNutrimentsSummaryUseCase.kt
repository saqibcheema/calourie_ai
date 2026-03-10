package com.example.calorieapp.domain.useCases

import com.example.calorieapp.domain.entities.DailyMacrosSummary
import com.example.calorieapp.domain.repository.BarcodeRepository

class GetTodayNutrimentsSummaryUseCase(
    private val repository: BarcodeRepository
) {
    suspend operator fun invoke(): DailyMacrosSummary? {
        return repository.getDailySummary()
    }
}