package com.example.calorieapp.domain.useCases

import android.graphics.Bitmap
import com.example.calorieapp.data.DataSource.remote.dto.VisionFoodResult
import com.example.calorieapp.domain.repository.OpenRouterVisionRepository
import javax.inject.Inject

class AnalyzeFoodImageUseCase @Inject constructor(
    private val repository: OpenRouterVisionRepository
) {
    suspend operator fun invoke(bitmap: Bitmap): Result<VisionFoodResult> =
        repository.analyzeFoodImage(bitmap)
}
