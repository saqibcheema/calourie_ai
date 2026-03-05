package com.example.calorieapp.domain.useCases

import com.example.calorieapp.domain.entities.DailySummary
import com.example.calorieapp.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailySummaryUseCase @Inject constructor(
    private val repository: MealRepository
) {
    operator fun invoke(selectedDate: String) : Flow<DailySummary?> {
        return repository.getDailySummary(selectedDate)
    }

}