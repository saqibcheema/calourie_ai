package com.example.calorieapp.data.Models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "scanned_products",
    indices = [Index(value = ["barcode"])])
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
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
    val scannedAt: Date = Date(),
    val quantity: Int = 1,
    val isDeleted: Boolean = false  // Soft delete
)