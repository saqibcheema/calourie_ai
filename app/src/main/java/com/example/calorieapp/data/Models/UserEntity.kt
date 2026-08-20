package com.example.calorieapp.data.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

// ─── Issue #1 Fix: Added goalPace, medicalConditions, pregnancyStatus columns
// ─── Issue #3 Fix: weight changed from Int → Float to preserve decimal precision
@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,

    val gender: String,
    val age: Int,
    val weight: Float,                          // Fix #3: was Int — now Float for 70.5kg etc.
    val heightFeet: Int,
    val heightInches: Int,
    val activityLevel: String,
    val goal: String,

    // ── New columns (Fix #1) — default values for existing rows via Migration ──
    val goalPace: String = "Moderate",
    val medicalConditions: String = "",         // Fix #7: stored as comma-separated String
    val pregnancyStatus: String = "None",
)