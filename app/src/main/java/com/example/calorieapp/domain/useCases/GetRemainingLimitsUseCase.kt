package com.example.calorieapp.domain.useCases

import com.example.calorieapp.domain.repository.LimitsRepository
import com.example.calorieapp.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class RemainingLimits(
    val tierType: String,
    val remainingBarcodeScans: Int,
    val remainingManualEntries: Int,
    val remainingAiVision: Int,
    val isAiVisionUnlocked: Boolean
)

class GetRemainingLimitsUseCase @Inject constructor(
    private val limitsRepository: LimitsRepository
) {
    operator fun invoke(): Flow<RemainingLimits> {
        return limitsRepository.getUserStatusFlow().combine(limitsRepository.getDailyRequestsFlow()) { status, requests ->
            val tier = status?.tierType ?: Constants.TIER_FREE
            
            val barcodeUsed = requests?.productScanRequestUsed ?: 0
            val manualUsed = requests?.manualEntryRequestUsed ?: 0
            val aiVisionUsed = requests?.aiVisionRequestUsed ?: 0

            if (tier == Constants.TIER_MEGA) {
                return@combine RemainingLimits(
                    tierType = tier,
                    remainingBarcodeScans = 999, // Infinite
                    remainingManualEntries = 999, // Infinite
                    remainingAiVision = maxOf(0, Constants.MEGA_AI_VISION_LIMIT - aiVisionUsed),
                    isAiVisionUnlocked = true
                )
            }
            
            if (tier == Constants.TIER_PREMIUM) {
                return@combine RemainingLimits(
                    tierType = tier,
                    remainingBarcodeScans = maxOf(0, Constants.PRO_PRODUCT_SCAN_LIMIT - barcodeUsed),
                    remainingManualEntries = maxOf(0, Constants.PRO_MANUAL_ENTRY_LIMIT - manualUsed),
                    remainingAiVision = maxOf(0, Constants.PRO_AI_VISION_LIMIT - aiVisionUsed),
                    isAiVisionUnlocked = true
                )
            }
            
            // Free Tier
            RemainingLimits(
                tierType = tier,
                remainingBarcodeScans = maxOf(0, Constants.FREE_PRODUCT_SCAN_LIMIT - barcodeUsed),
                remainingManualEntries = maxOf(0, Constants.FREE_MANUAL_ENTRY_LIMIT - manualUsed),
                remainingAiVision = 0,
                isAiVisionUnlocked = false
            )
        }
    }
}
