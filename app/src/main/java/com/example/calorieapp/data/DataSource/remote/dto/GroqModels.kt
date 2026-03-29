package com.example.calorieapp.data.DataSource.remote.dto

import com.google.gson.annotations.SerializedName

// Request
data class GroqChatRequest(
    val model: String = "llama-3.3-70b-versatile",
    val messages: List<GroqMessage>,
    @SerializedName("response_format")
    val responseFormat: GroqResponseFormat = GroqResponseFormat(),
    val temperature: Double = 0.3,
    @SerializedName("max_tokens")
    val maxTokens: Int = 500
)

data class GroqMessage(
    val role: String,
    val content: String
)

data class GroqResponseFormat(
    val type: String = "json_object"
)

// Response
data class GroqChatResponse(
    val choices: List<GroqChoice>
)

data class GroqChoice(
    val message: GroqMessage
)

// Itemized breakdown for multi-item meals
data class FoodItemEstimate(
    val name: String,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double
)

// Parsed nutrition result (to be used in domain/data)
data class ClarificationQuestion(
    val question: String,
    val options: List<String>
)

data class NutritionEstimate(
    val isClarificationNeeded: Boolean = false,
    val clarificationQuestions: List<ClarificationQuestion> = emptyList(),
    
    val displayName: String = "Meal",  // AI-generated concise display name (e.g. "Roti, Eggs & Tea")
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val fiber: Double = 0.0,
    val sugars: Double = 0.0,
    
    val items: List<FoodItemEstimate> = emptyList(), // Itemized breakdown
    
    val confidence: String = "medium" // "high", "medium", or "low"
)
