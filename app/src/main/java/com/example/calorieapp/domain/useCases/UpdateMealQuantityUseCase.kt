package com.example.calorieapp.domain.useCases

import com.example.calorieapp.domain.repository.BarcodeRepository
import javax.inject.Inject

class UpdateMealQuantityUseCase @Inject constructor(
    private val repository: BarcodeRepository
) {
    suspend operator fun invoke(barcode: String, newQuantity: Int) {
        repository.updateMealQuantity(barcode, newQuantity)
    }
}
