package com.example.calorieapp.domain.repository

import android.graphics.Bitmap
import com.example.calorieapp.data.DataSource.remote.dto.VisionFoodResult

interface GeminiVisionRepository {
    suspend fun analyzeFoodImage(bitmap: Bitmap): Result<VisionFoodResult>
}
