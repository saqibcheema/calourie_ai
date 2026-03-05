package com.example.calorieapp.data.repository

import com.example.calorieapp.data.DataSource.local.MealDao
import com.example.calorieapp.data.Models.MealLogEntity
import com.example.calorieapp.domain.entities.DailySummary
import com.example.calorieapp.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao
) : MealRepository {
    override suspend fun addMeal(meal: MealLogEntity) {
        mealDao.insertMeal(meal)
    }

    override fun getDailySummary(selectedDate: String): Flow<DailySummary?> {
        return mealDao.getDailySummary(selectedDate)
    }
}