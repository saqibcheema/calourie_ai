package com.example.calorieapp.domain.useCases

import com.example.calorieapp.domain.entities.Product
import com.example.calorieapp.domain.repository.BarcodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMealsByDateUseCase @Inject constructor(
    private val repository: BarcodeRepository
) {
    operator fun invoke(selectedDate: String): Flow<List<Product>> {
        return repository.getMealsByDate(selectedDate)
    }
}
