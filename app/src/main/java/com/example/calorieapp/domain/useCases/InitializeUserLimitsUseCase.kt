package com.example.calorieapp.domain.useCases

import com.example.calorieapp.data.Models.SubscriptionStatus
import com.example.calorieapp.data.Models.UserDailyRequest
import com.example.calorieapp.domain.repository.LimitsRepository
import com.example.calorieapp.util.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class InitializeUserLimitsUseCase @Inject constructor(
    private val limitsRepository: LimitsRepository
) {
    suspend operator fun invoke() {
        val status = limitsRepository.getUserStatus()
        if (status == null) {
            val newStatus = SubscriptionStatus(
                id = 1,
                tierType = Constants.TIER_FREE,
                expiryDate = "",
                token = null
            )
            limitsRepository.updateUserStatus(newStatus)
        }

        val dailyRequests = limitsRepository.getDailyRequests()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if (dailyRequests == null) {
            val newRequest = UserDailyRequest(
                id = 1,
                currentDate = todayStr,
                aiVisionRequestUsed = 0,
                productScanRequestUsed = 0,
                manualEntryRequestUsed = 0,
                adsWatched = 0
            )
            limitsRepository.resetDailyRequests(newRequest)
        }
    }
}
