package com.example.calorieapp.data.DataSource.remote

import com.example.calorieapp.data.DataSource.remote.dto.GroqChatRequest
import com.example.calorieapp.data.DataSource.remote.dto.GroqChatResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GroqApiService {
    @POST("openai/v1/chat/completions")
    suspend fun estimateNutrition(
        @Body request: GroqChatRequest
    ): GroqChatResponse
}
