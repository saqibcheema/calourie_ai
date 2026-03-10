package com.example.calorieapp.domain.useCases

import com.example.calorieapp.data.Models.MealLogEntity
import javax.inject.Inject

class AddMealUseCase @Inject constructor(
    private val repository: MealRepository
) {
    suspend operator fun invoke(meal: MealLogEntity){
        repository.addMeal(meal)
    }
}