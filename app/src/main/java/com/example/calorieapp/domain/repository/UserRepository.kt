package com.example.calorieapp.domain.repository

import com.example.calorieapp.domain.entities.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun saveUser(user: UserProfile)
    fun getUser(): Flow<UserProfile?>
}