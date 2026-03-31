package com.example.calorieapp.data.DataSource.remote

import android.graphics.Bitmap
import com.example.calorieapp.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiVisionService @Inject constructor() {

    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    private val systemPrompt = """
        You are a food recognition engine. Analyze the image and identify ALL distinct food items visible.
        Return ONLY a valid JSON object in this exact format, nothing else:
        {
          "items": [
            { "name": "Food name", "estimatedPortion": "medium plate" },
            { "name": "Another food", "estimatedPortion": "1 glass" }
          ]
        }
        Rules:
        - Use clear English names (e.g. "Chicken Biryani", "Naan", "Raita", "Fried Egg")
        - "estimatedPortion" is optional — include it only when clearly visible in the image
        - If you cannot identify any food at all, return: { "items": [] }
        - Return ONLY the JSON object. No markdown, no explanation, no extra text.
    """.trimIndent()

    suspend fun analyzeImage(bitmap: Bitmap): String {
        val inputContent = content {
            image(bitmap)
            text(systemPrompt)
        }
        return model.generateContent(inputContent).text ?: ""
    }
}
