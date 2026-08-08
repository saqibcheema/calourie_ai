package com.example.calorieapp.domain.useCases

import com.example.calorieapp.data.Models.UserDailyRequest
import com.example.calorieapp.domain.repository.LimitsRepository
import com.example.calorieapp.util.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class CheckDailyLimitUseCase @Inject constructor(
    private val limitsRepository: LimitsRepository
) {
    suspend operator fun invoke(featureType: FeatureType): Boolean {
        val status = limitsRepository.getUserStatus()
        val tier = status?.tierType ?: Constants.TIER_FREE

        var dailyRequests = limitsRepository.getDailyRequests()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (dailyRequests == null || dailyRequests.currentDate != todayStr) {
            // Reset for a new day
            val newRequest = UserDailyRequest(
                id = 1,
                currentDate = todayStr,
                aiVisionRequestUsed = 0,
                productScanRequestUsed = 0,
                manualEntryRequestUsed = 0,
                adsWatched = 0
            )
            limitsRepository.resetDailyRequests(newRequest)
            dailyRequests = newRequest
        }

        if (tier == Constants.TIER_MEGA) {
            return when (featureType) {
                FeatureType.AI_VISION -> dailyRequests.aiVisionRequestUsed < Constants.MEGA_AI_VISION_LIMIT
                else -> true
            }
        }

        if (tier == Constants.TIER_PREMIUM) {
            return when (featureType) {
                FeatureType.AI_VISION -> dailyRequests.aiVisionRequestUsed < Constants.PRO_AI_VISION_LIMIT
                FeatureType.PRODUCT_SCAN -> dailyRequests.productScanRequestUsed < Constants.PRO_PRODUCT_SCAN_LIMIT
                FeatureType.MANUAL_ENTRY -> dailyRequests.manualEntryRequestUsed < Constants.PRO_MANUAL_ENTRY_LIMIT
            }
        }

        // Free Tier Checking
        return when (featureType) {
            FeatureType.AI_VISION -> dailyRequests.aiVisionRequestUsed < Constants.FREE_AI_VISION_LIMIT
            FeatureType.PRODUCT_SCAN -> dailyRequests.productScanRequestUsed < Constants.FREE_PRODUCT_SCAN_LIMIT
            FeatureType.MANUAL_ENTRY -> dailyRequests.manualEntryRequestUsed < Constants.FREE_MANUAL_ENTRY_LIMIT
        }
    }

    enum class FeatureType {
        AI_VISION, PRODUCT_SCAN, MANUAL_ENTRY
    }
}
