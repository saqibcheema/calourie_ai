package com.example.calorieapp.data.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_daily_request")
class UserDailyRequest(
    @PrimaryKey val id : Int = 1,
    val currentDate : String,
    val aiVisionRequestUsed : Int,
    val productScanRequestUsed : Int,
    val manualEntryRequestUsed : Int,
    val adsWatched : Int
)