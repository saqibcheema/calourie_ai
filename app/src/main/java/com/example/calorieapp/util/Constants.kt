package com.example.calorieapp.util

object Constants {
    // Tiers
    const val TIER_FREE = "FREE"
    const val TIER_PREMIUM = "PREMIUM"
    const val TIER_MEGA = "MEGA"

    // Free Tier Daily Limits
    const val FREE_PRODUCT_SCAN_LIMIT = 5
    const val FREE_MANUAL_ENTRY_LIMIT = 5
    const val FREE_AI_VISION_LIMIT = 0 // Locked for free tier

    // Premium (Pro) Tier Daily Limits
    const val PRO_PRODUCT_SCAN_LIMIT = 50
    const val PRO_MANUAL_ENTRY_LIMIT = 50
    const val PRO_AI_VISION_LIMIT = 20

    // Mega Tier Daily Limits
    const val MEGA_AI_VISION_LIMIT = 100
}
