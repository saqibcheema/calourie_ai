package com.example.calorieapp.data.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,

    val gender: String,
    val age: Int,
    val weight: Int,
    val heightFeet: Int,
    val heightInches: Int,
    val activityLevel: String,
    val goal: String,

)