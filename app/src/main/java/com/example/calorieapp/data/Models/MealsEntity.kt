package com.example.calorieapp.data.Models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal_log",
    indices = [Index(value = ["date"])]
)
data class MealLogEntity(
    @PrimaryKey(autoGenerate = true)
    val logId: Int = 0,

    val date: String,      // Format: "yyyy-MM-dd"
    val foodName: String,
    val calories: Int,
    val protein: Int,
    val fats: Int,
    val carbs: Int,
    val mealType: String   // Breakfast, Lunch, Dinner, Snack
)