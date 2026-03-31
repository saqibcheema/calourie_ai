package com.example.calorieapp.data.repository

import android.graphics.Bitmap
import com.example.calorieapp.data.DataSource.remote.dto.DetectedFoodItem
import com.example.calorieapp.data.DataSource.remote.dto.VisionFoodResult
import com.example.calorieapp.domain.repository.GeminiVisionRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Mock implementation of GeminiVisionRepository for testing
 * when Gemini API quota is exceeded.
 *
 * Returns realistic fake food items with a simulated 2-second delay
 * so the analyzing animation is visible.
 *
 * TO RESTORE REAL API: In AppModule.kt, change
 *   provideGeminiVisionRepository(impl: GeminiVisionRepositoryMock)
 *   back to:
 *   provideGeminiVisionRepository(impl: GeminiVisionRepositoryImpl)
 */
class GeminiVisionRepositoryMock @Inject constructor() : GeminiVisionRepository {

    override suspend fun analyzeFoodImage(bitmap: Bitmap): Result<VisionFoodResult> {
        // Simulate real API latency so the loading animation is visible
        delay(2000L)

        val mockItems = listOf(
            DetectedFoodItem(
                name = "Chicken Biryani",
                estimatedPortion = "1 plate"
            ),
            DetectedFoodItem(
                name = "Raita",
                estimatedPortion = "1 small bowl"
            ),
            DetectedFoodItem(
                name = "Naan",
                estimatedPortion = "2 pieces"
            )
        )

        return Result.success(VisionFoodResult(items = mockItems))
    }
}
