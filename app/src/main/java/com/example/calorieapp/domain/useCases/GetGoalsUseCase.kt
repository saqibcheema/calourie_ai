package com.example.calorieapp.domain.useCases

import com.example.calorieapp.domain.entities.DailyGoals
import com.example.calorieapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGoalsUseCase @Inject constructor (
    private val repository: UserRepository
) {
    operator fun invoke(timestamp: Long? = null) : Flow<DailyGoals?> {
        return if (timestamp == null) {
            repository.getGoals()
        } else {
            repository.getGoalForDate(timestamp)
        }
    }
}