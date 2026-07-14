package com.example.calorieapp.data.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscription_status")
data class SubscriptionStatus(
    @PrimaryKey val id : Int = 1,
    val tierType : String,
    val expiryDate : String,
    val token : String?
)
