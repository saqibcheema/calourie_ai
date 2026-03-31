package com.example.calorieapp.data.DataSource.remote.dto

data class VisionFoodResult(
    val items: List<DetectedFoodItem> = emptyList()
)

data class DetectedFoodItem(
    val name: String,
    val estimatedPortion: String? = null
)
