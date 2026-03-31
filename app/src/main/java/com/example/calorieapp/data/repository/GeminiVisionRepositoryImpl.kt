package com.example.calorieapp.data.repository

import android.graphics.Bitmap
import com.example.calorieapp.data.DataSource.remote.GeminiVisionService
import com.example.calorieapp.data.DataSource.remote.dto.DetectedFoodItem
import com.example.calorieapp.data.DataSource.remote.dto.VisionFoodResult
import com.example.calorieapp.domain.repository.GeminiVisionRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import javax.inject.Inject

class GeminiVisionRepositoryImpl @Inject constructor(
    private val service: GeminiVisionService,
    private val gson: Gson
) : GeminiVisionRepository {

    override suspend fun analyzeFoodImage(bitmap: Bitmap): Result<VisionFoodResult> {
        return try {
            val rawResponse = service.analyzeImage(bitmap)

            if (rawResponse.isBlank()) {
                return Result.failure(Exception("Gemini returned an empty response."))
            }

            // Robustly extract JSON object (same pattern as GroqNutritionRepositoryImpl)
            val jsonMatch = Regex("\\{.*\\}", setOf(RegexOption.DOT_MATCHES_ALL)).find(rawResponse)
            val sanitizedJson = jsonMatch?.value
                ?: return Result.failure(Exception("Could not parse Gemini response."))

            val jsonObject = gson.fromJson(sanitizedJson, JsonObject::class.java)
            val itemsArray = jsonObject.getAsJsonArray("items")

            val items = mutableListOf<DetectedFoodItem>()
            itemsArray?.forEach { element ->
                val obj = element.asJsonObject
                val name = obj.get("name")?.asString ?: return@forEach
                val portion = obj.get("estimatedPortion")?.asString
                items.add(DetectedFoodItem(name = name, estimatedPortion = portion))
            }

            if (items.isEmpty()) {
                Result.failure(Exception("No food items detected in the image. Please try a clearer photo."))
            } else {
                Result.success(VisionFoodResult(items = items))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
