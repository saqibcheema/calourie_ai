package com.example.calorieapp.data.DataSource.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("product_name")
    val productName: String?,

    @SerializedName("brands")
    val brand: String?,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("nutriments")
    val nutriments: NutrimentsDto?,

    @SerializedName("serving_size")
    val servingSize: String?,

    @SerializedName("product_quantity")
    val productQuantity: Double?,

    @SerializedName("quantity_per_unit_value")
    val quantityPerUnitValue: Double?
)

data class ProductResponseDto(
    @SerializedName("status")
    val status: Int,

    @SerializedName("product")
    val product: ProductDto?
)