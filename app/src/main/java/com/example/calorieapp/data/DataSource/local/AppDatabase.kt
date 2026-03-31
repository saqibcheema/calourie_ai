package com.example.calorieapp.data.DataSource.local

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.calorieapp.data.Models.*

@Database(entities = [UserEntity::class, GoalsEntity::class, ProductEntity::class, ScannedProductEntity::class], version = 7, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun ProductDao(): ProductDao
    abstract fun scannedProductDao(): ScannedProductDao

    companion object {
        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE goals_table ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}