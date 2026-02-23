package com.example.calorieapp.domain.repository

import com.example.calorieapp.domain.entities.DailyGoals
import com.example.calorieapp.domain.entities.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun saveUser(user: UserProfile, goals: DailyGoals)
    fun getUser(): Flow<UserProfile?>
    fun getGoals() : Flow<DailyGoals?>
}