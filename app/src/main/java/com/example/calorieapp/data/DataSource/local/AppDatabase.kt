package com.example.calorieapp.data.DataSource.local

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.calorieapp.data.Models.*

@Database(
    entities = [
        UserEntity::class,
        GoalsEntity::class,
        ProductEntity::class,
        ScannedProductEntity::class
        // WeightHistoryEntity removed — migrated out in v9
    ],
    version = 9,
    exportSchema = false
)
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

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `weight_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `weightValue` REAL NOT NULL, `timestamp` INTEGER NOT NULL)")
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Weight tracking feature removed — drop the table cleanly
                db.execSQL("DROP TABLE IF EXISTS `weight_history`")
            }
        }
    }
}