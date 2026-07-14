package com.example.calorieapp.data.DataSource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.calorieapp.data.Models.UserDailyRequest

@Dao
interface DailyRequestsDao{
    @Query("SELECT * FROM user_daily_request WHERE id = 1")
    suspend fun getDailyRequests(): UserDailyRequest?

    @Query("UPDATE user_daily_request SET aiVisionRequestUsed = aiVisionRequestUsed + 1 WHERE id = 1")
    suspend fun updateAiVisionRequest()

    @Query("UPDATE user_daily_request SET productScanRequestUsed = productScanRequestUsed + 1 WHERE id = 1")
    suspend fun updateProductScanRequest()

    @Query("UPDATE user_daily_request SET manualEntryRequestUsed = manualEntryRequestUsed + 1 WHERE id = 1")
    suspend fun updateManualEntryRequest()

    @Query("UPDATE user_daily_request SET adsWatched = adsWatched + 1 WHERE id = 1")
    suspend fun updateAdsWatched()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun resetDailyRequests(request: UserDailyRequest)
}