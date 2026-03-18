package com.example.calorieapp.data.Models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "scanned_products_cache",
    indices = [Index(value = ["barcode"])])
data class ScannedProductEntity(
    @PrimaryKey
    val barcode: String,
    val productName: String?,
    val brand: String?,
    val imageUrl: String?,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double?,
    val sugars: Double?,
    val scannedAt: Date = Date()
)
