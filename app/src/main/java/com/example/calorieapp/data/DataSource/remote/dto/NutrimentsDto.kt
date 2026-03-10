package com.example.calorieapp.data.DataSource.remote.dto

import com.google.gson.annotations.SerializedName

data class NutrimentsDto(
    @SerializedName("energy-kcal_100g")
    val calories: Double?,

    @SerializedName("proteins_100g")
    val protein: Double?,

    @SerializedName("carbohydrates_100g")
    val carbs: Double?,

    @SerializedName("fat_100g")
    val fat: Double?,

    @SerializedName("fiber_100g")
    val fiber: Double?,

    @SerializedName("sugars_100g")
    val sugars: Double?
)