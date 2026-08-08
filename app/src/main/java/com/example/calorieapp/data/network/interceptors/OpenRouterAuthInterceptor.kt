package com.example.calorieapp.data.network.interceptors

import com.example.calorieapp.util.RemoteConfigHelper
import okhttp3.Interceptor
import okhttp3.Response

class OpenRouterAuthInterceptor(private val remoteConfigHelper: RemoteConfigHelper) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val apiKey = remoteConfigHelper.getOpenRouterApiKey()
        val req = chain.request().newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .header("X-Title", "Calourie AI")
            .build()
        return chain.proceed(req)
    }
}
