package com.example.calorieapp.domain.useCases

import com.example.calorieapp.domain.entities.DailyGoals
import com.example.calorieapp.domain.entities.UserProfile
import com.example.calorieapp.domain.repository.UserRepository

class SaveUserAndCalculateGoalsUseCase (
    private val repository: UserRepository
){
    suspend operator fun invoke(user: UserProfile): DailyGoals{

        repository.saveUser(user)

        val dailyGoals = CalculationUtils.calculateGoals(user)
        return dailyGoals
    }
}