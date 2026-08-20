package com.example.calorieapp.domain.entities

data class UserProfile (
    val gender: String,
    val age: Int,
    val weight: String,
    val heightFeet: Int,
    val heightInches: Int,
    val activityLevel: String,
    val goal: String,
    // ─── New fields for accurate calculation ─────────────────────────────────
    val goalPace: String = "Moderate",              // "Slow" | "Moderate" | "Fast"
    val medicalConditions: List<String> = emptyList(), // ["Diabetes", "Thyroid", "PCOS", "Kidney", "Heart"]
    val pregnancyStatus: String = "None",           // "None" | "1st Trimester" | "2nd Trimester" | "3rd Trimester" | "Breastfeeding"
)