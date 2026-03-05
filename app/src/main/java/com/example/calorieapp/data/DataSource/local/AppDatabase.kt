package com.example.calorieapp.data.DataSource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.calorieapp.data.Models.GoalsEntity
import com.example.calorieapp.data.Models.MealLogEntity
import com.example.calorieapp.data.Models.UserEntity

@Database(entities = [UserEntity::class, GoalsEntity::class, MealLogEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun MealDao(): MealDao
}