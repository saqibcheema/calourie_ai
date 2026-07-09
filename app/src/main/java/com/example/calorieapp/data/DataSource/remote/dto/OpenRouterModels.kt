package com.example.calorieapp.data.DataSource.remote.dto

data class OpenRouterVisionRequest(
    val model: String = "google/gemini-2.5-flash-lite",
    val messages : List<OpenRouterMessage>
)

data class OpenRouterMessage(
    val role : String,
    val content : List<OpenRouterContent>
)

data class OpenRouterContent(
    val type : String,
    val text : String? = null,
    val image_url : OpenRouterImageUrl? = null
)

data class OpenRouterImageUrl(
    val url : String
)

data class OpenRouterVisionResponse(
    val choices : List<OpenRouterVisionChoice>
)

data class OpenRouterVisionChoice(
    val message : OpenRouterMessageResponse
)

data class OpenRouterMessageResponse(
    val content : String
)