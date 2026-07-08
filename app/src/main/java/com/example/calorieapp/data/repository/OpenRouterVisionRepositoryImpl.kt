package com.example.calorieapp.data.repository

import android.graphics.Bitmap
import com.example.calorieapp.data.DataSource.remote.OpenRouterVisionService
import com.example.calorieapp.data.DataSource.remote.dto.DetectedFoodItem
import com.example.calorieapp.data.DataSource.remote.dto.VisionFoodResult
import com.example.calorieapp.domain.repository.OpenRouterVisionRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import javax.inject.Inject

class OpenRouterVisionRepositoryImpl @Inject constructor(
    private val service: OpenRouterVisionService,
    private val gson: Gson
) : OpenRouterVisionRepository {

    override suspend fun analyzeFoodImage(bitmap: Bitmap): Result<VisionFoodResult> {
        return try {
            val rawResponse = service.analyseImage(bitmap)

            if (rawResponse.isBlank()) {
                return Result.failure(Exception("Vision API returned an empty response."))
            }

            // Robustly extract JSON object
            val jsonMatch = Regex("\\{.*\\}", setOf(RegexOption.DOT_MATCHES_ALL)).find(rawResponse)
            val sanitizedJson = jsonMatch?.value
                ?: return Result.failure(Exception("Could not parse Vision API response."))

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
            val friendlyMessage = when {
                e.message?.contains("API_KEY_INVALID", ignoreCase = true) == true ||
                e.message?.contains("API key not valid", ignoreCase = true) == true ->
                    "Invalid OpenRouter API key."
                e.message?.contains("RESOURCE_EXHAUSTED", ignoreCase = true) == true ||
                e.message?.contains("quota", ignoreCase = true) == true ->
                    "OpenRouter API quota exceeded."
                e.message?.contains("Unable to resolve host", ignoreCase = true) == true ||
                e.message?.contains("timeout", ignoreCase = true) == true ->
                    "Network error. Please check your internet connection and try again."
                else -> "Vision error: ${e.message ?: "Unknown error"}"
            }
            Result.failure(Exception(friendlyMessage))
        }
    }
}
