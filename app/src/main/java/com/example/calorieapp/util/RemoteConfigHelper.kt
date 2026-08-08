package com.example.calorieapp.util

import com.example.calorieapp.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigHelper @Inject constructor() {
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 3600 else 43200 // 1 hr debug, 12 hrs prod
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        
        // Use BuildConfig keys as fallback defaults in case Remote Config fails to load
        remoteConfig.setDefaultsAsync(
            mapOf(
                "groq_api_key" to BuildConfig.GROQ_API_KEY,
                "openrouter_api_key" to BuildConfig.OPENROUTER_API_KEY,
                "groq_model_name" to "llama-3.3-70b-versatile",
                "openrouter_model_name" to "google/gemini-2.5-flash-lite"
            )
        )
    }

    suspend fun fetchAndActivate() {
        try {
            remoteConfig.fetchAndActivate().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getGroqApiKey(): String {
        val key = remoteConfig.getString("groq_api_key")
        return key.ifEmpty { BuildConfig.GROQ_API_KEY }
    }

    fun getOpenRouterApiKey(): String {
        val key = remoteConfig.getString("openrouter_api_key")
        return key.ifEmpty { BuildConfig.OPENROUTER_API_KEY }
    }

    fun getGroqModelName(): String {
        val model = remoteConfig.getString("groq_model_name")
        return model.ifEmpty { "llama-3.3-70b-versatile" }
    }

    fun getOpenRouterModelName(): String {
        val model = remoteConfig.getString("openrouter_model_name")
        return model.ifEmpty { "google/gemini-2.5-flash-lite" }
    }
}
