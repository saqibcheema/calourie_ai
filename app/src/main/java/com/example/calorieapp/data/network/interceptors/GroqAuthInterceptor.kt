package com.example.calorieapp.data.network.interceptors

import com.example.calorieapp.util.RemoteConfigHelper
import okhttp3.Interceptor
import okhttp3.Response

class GroqAuthInterceptor(private val remoteConfigHelper: RemoteConfigHelper) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val apiKey = remoteConfigHelper.getGroqApiKey()
        val req = chain.request().newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .build()
        return chain.proceed(req)
    }
}
