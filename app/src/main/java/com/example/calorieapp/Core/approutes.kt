package com.example.calorieapp.Core

import kotlinx.serialization.Serializable

sealed interface Dest{
    @Serializable
    data object OnBoarding : Dest

    @Serializable
    data object Dashboard : Dest
}