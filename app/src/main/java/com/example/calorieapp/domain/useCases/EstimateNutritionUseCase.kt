package com.example.calorieapp.domain.useCases

import com.example.calorieapp.data.DataSource.remote.dto.NutritionEstimate
import com.example.calorieapp.domain.repository.GroqNutritionRepository
import javax.inject.Inject

class EstimateNutritionUseCase @Inject constructor(
    private val repository: GroqNutritionRepository
) {
    suspend operator fun invoke(foodDescription: String): Result<NutritionEstimate> {
        return repository.estimateNutrition(foodDescription)
    }
}
