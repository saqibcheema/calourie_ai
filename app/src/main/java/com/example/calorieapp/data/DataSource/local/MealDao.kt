package com.example.calorieapp.data.DataSource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.calorieapp.data.Models.MealLogEntity
import com.example.calorieapp.domain.entities.DailySummary
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealLogEntity)

    @Query("""
        SELECT 
            SUM(calories) as totalCalories, 
            SUM(protein) as totalProtein, 
            SUM(fats) as totalFats, 
            SUM(carbs) as totalCarbs 
        FROM meal_log 
        WHERE date = :selectedDate
    """)
    fun getDailySummary(selectedDate: String): Flow<DailySummary?>
}
