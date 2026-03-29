package com.example.calorieapp.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.atomic.AtomicLong

class RateLimitInterceptor(private val cooldownMs: Long = 2000L) : Interceptor {
    private val lastRequestTime = AtomicLong(0L)

    override fun intercept(chain: Interceptor.Chain): Response {
        val now = System.currentTimeMillis()
        val last = lastRequestTime.get()
        if (now - last < cooldownMs) {
            throw RateLimitException()
        }
        lastRequestTime.set(now)
        return chain.proceed(chain.request())
    }
}
