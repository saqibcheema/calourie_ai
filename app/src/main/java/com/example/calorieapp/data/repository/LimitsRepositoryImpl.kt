package com.example.calorieapp.data.repository

import com.example.calorieapp.data.DataSource.local.DailyRequestsDao
import com.example.calorieapp.data.DataSource.local.UserStatusDao
import com.example.calorieapp.data.Models.SubscriptionStatus
import com.example.calorieapp.data.Models.UserDailyRequest
import com.example.calorieapp.domain.repository.LimitsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LimitsRepositoryImpl @Inject constructor(
    private val dailyRequestsDao: DailyRequestsDao,
    private val userStatusDao: UserStatusDao
) : LimitsRepository {

    override suspend fun getDailyRequests(): UserDailyRequest? {
        return dailyRequestsDao.getDailyRequests()
    }

    override fun getDailyRequestsFlow(): Flow<UserDailyRequest?> {
        return dailyRequestsDao.getDailyRequestsFlow()
    }

    override suspend fun getUserStatus(): SubscriptionStatus? {
        return userStatusDao.getUserStatus()
    }

    override fun getUserStatusFlow(): Flow<SubscriptionStatus?> {
        return userStatusDao.getUserStatusFlow()
    }

    override suspend fun updateAiVisionRequest() {
        dailyRequestsDao.updateAiVisionRequest()
    }

    override suspend fun updateProductScanRequest() {
        dailyRequestsDao.updateProductScanRequest()
    }

    override suspend fun updateManualEntryRequest() {
        dailyRequestsDao.updateManualEntryRequest()
    }

    override suspend fun resetDailyRequests(request: UserDailyRequest) {
        dailyRequestsDao.resetDailyRequests(request)
    }

    override suspend fun updateUserStatus(subscriptionStatus: SubscriptionStatus) {
        userStatusDao.updateUserStatus(subscriptionStatus)
    }
}
