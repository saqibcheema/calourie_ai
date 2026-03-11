package com.example.calorieapp.domain.useCases

import com.example.calorieapp.domain.entities.Product
import com.example.calorieapp.domain.repository.BarcodeRepository
import javax.inject.Inject

class AddMealUseCase @Inject constructor(
    private val repository: BarcodeRepository
) {
    suspend operator fun invoke(meal: Product){
        repository.addMeal(meal)
    }
}