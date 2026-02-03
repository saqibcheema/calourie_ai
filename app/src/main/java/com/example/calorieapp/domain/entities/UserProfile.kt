package com.example.calorieapp.domain.entities

data class UserProfile (
    val gender: String,
    val age: Int,
    val weight: String,
    val heightFeet: Int,
    val heightInches: Int,
    val activityLevel: String,
    val goal: String
)