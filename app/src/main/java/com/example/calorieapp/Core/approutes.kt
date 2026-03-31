package com.example.calorieapp.Core

import kotlinx.serialization.Serializable

sealed interface Dest{
    @Serializable
    data object OnBoarding : Dest

    @Serializable
    data object MainScreen : Dest

    @Serializable
    data object Dashboard : Dest

    @Serializable
    data object Profile : Dest

    @Serializable
    data object ManualEntry : Dest
}