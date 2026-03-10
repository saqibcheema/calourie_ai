package com.example.calorieapp.domain.entities

import java.util.Date

data class Product(
    val barcode: String,
    val productName: String,
    val brand: String?,
    val imageUrl: String?,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double?,
    val sugars: Double?,
    val scannedAt: Date = Date()
) {
    val isValid: Boolean
        get() = calories >= 0 && protein >= 0 && carbs >= 0 && fat >= 0

    val totalMacros: Double
        get() = protein + carbs + fat
}