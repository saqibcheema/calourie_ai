package com.example.calorieapp.data.DataSource.remote

import com.example.calorieapp.data.DataSource.remote.dto.OpenRouterVisionRequest
import com.example.calorieapp.data.DataSource.remote.dto.OpenRouterVisionResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenRouterApiService{
    @POST("chat/completions")
    suspend fun analyseImage(
        @Body request : OpenRouterVisionRequest
    ) : OpenRouterVisionResponse
}