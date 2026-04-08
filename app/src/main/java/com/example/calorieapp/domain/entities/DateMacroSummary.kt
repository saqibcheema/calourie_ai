package com.example.calorieapp.domain.entities

data class DateMacroSummary(
    val dateString: String,
    val totalCalories: Double?,
    val totalProtein: Double?,
    val totalFats: Double?,
    val totalCarbs: Double?
)
