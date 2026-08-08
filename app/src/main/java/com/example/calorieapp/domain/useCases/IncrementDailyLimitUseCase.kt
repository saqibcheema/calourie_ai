package com.example.calorieapp.domain.useCases

import com.example.calorieapp.domain.repository.LimitsRepository
import javax.inject.Inject

class IncrementDailyLimitUseCase @Inject constructor(
    private val limitsRepository: LimitsRepository
) {
    suspend operator fun invoke(featureType: CheckDailyLimitUseCase.FeatureType) {
        when (featureType) {
            CheckDailyLimitUseCase.FeatureType.AI_VISION -> limitsRepository.updateAiVisionRequest()
            CheckDailyLimitUseCase.FeatureType.PRODUCT_SCAN -> limitsRepository.updateProductScanRequest()
            CheckDailyLimitUseCase.FeatureType.MANUAL_ENTRY -> limitsRepository.updateManualEntryRequest()
        }
    }
}
