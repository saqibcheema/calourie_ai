package com.example.calorieapp.domain.useCases

import com.example.calorieapp.domain.entities.Product
import com.example.calorieapp.domain.repository.BarcodeRepository

class ScanProductUseCase(
    private val repository: BarcodeRepository
) {
    suspend operator fun invoke(barcode: String): Result<Product> {
        if (barcode.isBlank() || barcode.length < 8) {
            return Result.failure(
                IllegalArgumentException("Invalid barcode format")
            )
        }
        return repository.scanProduct(barcode)
    }
}