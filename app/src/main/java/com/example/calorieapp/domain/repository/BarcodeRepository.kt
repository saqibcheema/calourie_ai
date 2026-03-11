package com.example.calorieapp.domain.repository

import com.example.calorieapp.domain.entities.DailyMacrosSummary
import com.example.calorieapp.domain.entities.Product
import kotlinx.coroutines.flow.Flow

interface BarcodeRepository {
    suspend fun addMeal(meal: Product)
    suspend fun scanProduct(barcode: String): Result<Product>
    fun getScanHistory(): Flow<List<Product>>
    suspend fun deleteProduct(barcode: String)
    fun getDailySummary(selectedDate: String): Flow<DailyMacrosSummary?>
}