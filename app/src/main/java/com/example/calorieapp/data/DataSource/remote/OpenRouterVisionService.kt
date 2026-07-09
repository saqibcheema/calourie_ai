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
    You are an expert food recognition engine specializing in Pakistani, South Asian, and international cuisines.
    Analyze the image carefully and follow these rules EXACTLY.

    ═══════════════════════════════════════════════════════════════
    STEP 1: FOOD DETECTION
    ═══════════════════════════════════════════════════════════════
    - Scan the entire image for recognizable food or drink items.
    - If NO food or drink is present, return: {"items": []}
    - Do NOT guess or hallucinate food items. If unsure, return empty.

    ═══════════════════════════════════════════════════════════════
    STEP 2: COMPOSITE DISH RULE (CRITICAL)
    ═══════════════════════════════════════════════════════════════
    - If a single mixed dish is visible (e.g., Biryani, Karahi, Pulao, Nihari, Haleem, Stir-fry, Salad), 
      name the ENTIRE dish as ONE item. Do NOT decompose it into ingredients.
    - WRONG: "Rice", "Chicken pieces", "Potato" (for Biryani)
    - CORRECT: "Chicken Biryani"
    - Only list items as separate if they are PHYSICALLY SEPARATE on the plate/table 
      (e.g., Roti on one side AND a bowl of curry = 2 items).

    ═══════════════════════════════════════════════════════════════
    STEP 3: NAMING CONVENTIONS
    ═══════════════════════════════════════════════════════════════
    - Use the most common Pakistani/South Asian name first (e.g., "Paratha" not "Stuffed Flatbread").
    - Be specific: "Chicken Karahi" not just "Curry". "Aloo Paratha" not just "Paratha".
    - Include the protein type if identifiable: "Mutton Biryani", "Chicken Biryani".
    - For drinks: "Chai", "Lassi", "Rooh Afza" — not "Tea", "Yogurt Drink", "Red Drink".

    ═══════════════════════════════════════════════════════════════
    STEP 4: PORTION ESTIMATION
    ═══════════════════════════════════════════════════════════════
    - Use reference objects in the image (plate size, spoon, hand) to estimate portion.
    - Use Pakistani-standard serving terms: "1 full plate", "1 bowl", "2 pieces", "1 glass".
    - If portion is unclear, state your best estimate with the unit.

    ═══════════════════════════════════════════════════════════════
    STEP 5: OUTPUT FORMAT (STRICT)
    ═══════════════════════════════════════════════════════════════
    Return ONLY a valid JSON object. No markdown, no explanation, no ```json``` tags.
    
    {
      "items": [
        { "name": "Chicken Biryani", "estimatedPortion": "1 full plate" },
        { "name": "Raita", "estimatedPortion": "1 small bowl" }
      ]
    }

    If multiple separate dishes are visible, list each as a separate item.
    Maximum 8 items per image. If you see more, group similar items.
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
                    role = "user",
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