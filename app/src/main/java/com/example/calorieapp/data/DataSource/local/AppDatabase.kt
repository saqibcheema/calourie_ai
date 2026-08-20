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
        ScannedProductEntity::class,
        SubscriptionStatus::class,
        UserDailyRequest::class
        // WeightHistoryEntity removed — migrated out in v9
    ],
    version = 11,           // Fix #5: bumped from 10 → 11
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun ProductDao(): ProductDao
    abstract fun scannedProductDao(): ScannedProductDao
    abstract fun userStatusDao(): UserStatusDao
    abstract fun dailyRequestsDao(): DailyRequestsDao


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

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `subscription_status` (`id` INTEGER NOT NULL, `tierType` TEXT NOT NULL, `expiryDate` TEXT NOT NULL, `token` TEXT, PRIMARY KEY(`id`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `user_daily_request` (`id` INTEGER NOT NULL, `currentDate` TEXT NOT NULL, `aiVisionRequestUsed` INTEGER NOT NULL, `productScanRequestUsed` INTEGER NOT NULL, `manualEntryRequestUsed` INTEGER NOT NULL, `adsWatched` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            }
        }

        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Fix #1: Add 3 new columns with safe defaults for existing rows
                db.execSQL("ALTER TABLE user_table ADD COLUMN goalPace TEXT NOT NULL DEFAULT 'Moderate'")
                db.execSQL("ALTER TABLE user_table ADD COLUMN medicalConditions TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE user_table ADD COLUMN pregnancyStatus TEXT NOT NULL DEFAULT 'None'")

                // Fix #3: Migrate weight column from INTEGER → REAL (SQLite rename+recreate trick)
                // Step 1: Create new table with weight as REAL
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_table_new (
                        `id` INTEGER NOT NULL,
                        `gender` TEXT NOT NULL,
                        `age` INTEGER NOT NULL,
                        `weight` REAL NOT NULL,
                        `heightFeet` INTEGER NOT NULL,
                        `heightInches` INTEGER NOT NULL,
                        `activityLevel` TEXT NOT NULL,
                        `goal` TEXT NOT NULL,
                        `goalPace` TEXT NOT NULL DEFAULT 'Moderate',
                        `medicalConditions` TEXT NOT NULL DEFAULT '',
                        `pregnancyStatus` TEXT NOT NULL DEFAULT 'None',
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                // Step 2: Copy existing data (weight cast to REAL automatically)
                db.execSQL("""
                    INSERT INTO user_table_new 
                    SELECT id, gender, age, CAST(weight AS REAL), heightFeet, heightInches,
                           activityLevel, goal, 'Moderate', '', 'None'
                    FROM user_table
                """.trimIndent())
                // Step 3: Drop old table, rename new one
                db.execSQL("DROP TABLE user_table")
                db.execSQL("ALTER TABLE user_table_new RENAME TO user_table")
            }
        }
    }
}