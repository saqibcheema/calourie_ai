package com.example.calorieapp.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class OpenRouterAuthInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request().newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .header("X-Title", "Calourie AI")
            .build()
        return chain.proceed(req)
    }
}

