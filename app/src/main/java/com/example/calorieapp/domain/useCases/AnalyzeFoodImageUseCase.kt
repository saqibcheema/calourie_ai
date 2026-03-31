package com.example.calorieapp.domain.useCases

import android.graphics.Bitmap
import com.example.calorieapp.data.DataSource.remote.dto.VisionFoodResult
import com.example.calorieapp.domain.repository.GeminiVisionRepository
import javax.inject.Inject

class AnalyzeFoodImageUseCase @Inject constructor(
    private val repository: GeminiVisionRepository
) {
    suspend operator fun invoke(bitmap: Bitmap): Result<VisionFoodResult> =
        repository.analyzeFoodImage(bitmap)
}
