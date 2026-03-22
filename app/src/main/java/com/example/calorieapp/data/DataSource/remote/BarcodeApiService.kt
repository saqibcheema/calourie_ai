package com.example.calorieapp.data.DataSource.remote

import com.example.calorieapp.data.DataSource.remote.dto.ProductResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface BarcodeApiService {

    @GET("api/v2/product/{barcode}.json")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String
    ): ProductResponseDto
}