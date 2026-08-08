package com.example.calorieapp.domain.repository

import com.example.calorieapp.data.Models.SubscriptionStatus
import com.example.calorieapp.data.Models.UserDailyRequest
import kotlinx.coroutines.flow.Flow

interface LimitsRepository {
    suspend fun getDailyRequests(): UserDailyRequest?
    fun getDailyRequestsFlow(): Flow<UserDailyRequest?>
    suspend fun getUserStatus(): SubscriptionStatus?
    fun getUserStatusFlow(): Flow<SubscriptionStatus?>
    
    suspend fun updateAiVisionRequest()
    suspend fun updateProductScanRequest()
    suspend fun updateManualEntryRequest()
    
    suspend fun resetDailyRequests(request: UserDailyRequest)
    suspend fun updateUserStatus(subscriptionStatus: SubscriptionStatus)
}
