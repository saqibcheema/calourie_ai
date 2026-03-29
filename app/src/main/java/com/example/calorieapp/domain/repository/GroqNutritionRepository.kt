package com.example.calorieapp.domain.repository

import com.example.calorieapp.data.DataSource.remote.dto.NutritionEstimate

interface GroqNutritionRepository {
    suspend fun estimateNutrition(foodDescription: String): Result<NutritionEstimate>
}
