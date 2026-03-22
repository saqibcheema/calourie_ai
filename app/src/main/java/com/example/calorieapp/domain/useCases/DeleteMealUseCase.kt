package com.example.calorieapp.domain.useCases

import com.example.calorieapp.domain.repository.BarcodeRepository
import javax.inject.Inject

class DeleteMealUseCase @Inject constructor(
    private val repository: BarcodeRepository
) {
    suspend operator fun invoke(barcode: String) {
        repository.deleteProduct(barcode)
    }
}
