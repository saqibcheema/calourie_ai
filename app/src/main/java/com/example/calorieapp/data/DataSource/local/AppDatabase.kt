package com.example.calorieapp.data.DataSource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.calorieapp.data.Models.GoalsEntity
import com.example.calorieapp.data.Models.UserEntity

@Database(entities = [UserEntity::class, GoalsEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}