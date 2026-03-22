package com.example.calorieapp.data.DataSource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.calorieapp.data.Models.GoalsEntity
import com.example.calorieapp.data.Models.ProductEntity
import com.example.calorieapp.data.Models.ScannedProductEntity
import com.example.calorieapp.data.Models.UserEntity

@Database(entities = [UserEntity::class, GoalsEntity::class, ProductEntity::class, ScannedProductEntity::class], version = 5, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun ProductDao(): ProductDao
    abstract fun scannedProductDao(): ScannedProductDao
}