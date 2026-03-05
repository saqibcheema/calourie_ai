package com.example.calorieapp.domain.repository

import com.example.calorieapp.data.Models.MealLogEntity
import com.example.calorieapp.domain.entities.DailySummary
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    suspend fun addMeal(meal: MealLogEntity)
    fun getDailySummary(selectedDate: String): Flow<DailySummary?>
}


