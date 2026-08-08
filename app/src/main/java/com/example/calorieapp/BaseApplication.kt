package com.example.calorieapp

import android.app.Application
import com.example.calorieapp.util.RemoteConfigHelper
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application() {
    @Inject
    lateinit var remoteConfigHelper: RemoteConfigHelper

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            remoteConfigHelper.fetchAndActivate()
        }
    }
}