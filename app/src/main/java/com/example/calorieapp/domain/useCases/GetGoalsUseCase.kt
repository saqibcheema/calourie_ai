package com.example.calorieapp.domain.useCases

import com.example.calorieapp.domain.entities.DailyGoals
import com.example.calorieapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGoalsUseCase @Inject constructor (
    private val repository: UserRepository
) {
    operator fun invoke() : Flow<DailyGoals?> {
        val dailyGoals = repository.getGoals()
        return dailyGoals
    }
}