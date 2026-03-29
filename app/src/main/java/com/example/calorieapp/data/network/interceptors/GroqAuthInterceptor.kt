package com.example.calorieapp.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class GroqAuthInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request().newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .build()
        return chain.proceed(req)
    }
}
