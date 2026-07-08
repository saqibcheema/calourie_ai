package com.example.calorieapp.data.DataSource.remote

import android.graphics.Bitmap
import com.example.calorieapp.data.DataSource.remote.dto.OpenRouterContent
import com.example.calorieapp.data.DataSource.remote.dto.OpenRouterImageUrl
import com.example.calorieapp.data.DataSource.remote.dto.OpenRouterMessage
import com.example.calorieapp.data.DataSource.remote.dto.OpenRouterVisionRequest
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class OpenRouterVisionService @Inject constructor(
    private val api : OpenRouterApiService
){
    private val systemPrompt = """
        You are a food recognition engine. Analyze the image and identify ALL distinct food items visible.
        Return ONLY a valid JSON object in this exact format, nothing else:
        {
          "items": [
            { "name": "Food name", "estimatedPortion": "medium plate" }
          ]
        }
    """.trimIndent()

    suspend fun analyseImage(bitmap: Bitmap): String{
        var outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val base64Image = outputStream.toByteArray()
        val base64String = android.util.Base64.encodeToString(base64Image, android.util.Base64.NO_WRAP)

        val imageUrlData = "data:image/jpeg;base64,$base64String"
        val request = OpenRouterVisionRequest(
            messages = listOf(
                OpenRouterMessage(
                    role = "User",
                    content = listOf(
                        OpenRouterContent(
                            type = "text",
                            text = systemPrompt
                        ),
                        OpenRouterContent(
                            type = "image_url",
                            image_url = OpenRouterImageUrl(url = imageUrlData)
                        )
                    )
                    )
                )
            )
        val response = api.analyseImage(request)
        return response.choices.firstOrNull()?.message?.content ?: ""
    }
}