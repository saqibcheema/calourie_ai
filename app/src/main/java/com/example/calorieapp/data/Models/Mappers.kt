package com.example.calorieapp.data.Models

import com.example.calorieapp.domain.entities.DailyGoals
import com.example.calorieapp.domain.entities.Product
import com.example.calorieapp.domain.entities.UserProfile

// ── Issue #2 Fix: safe float parsing — no crash on decimal weight (e.g. "70.5")
// ── Issue #6 Fix: all 3 new fields are now mapped in BOTH directions
fun UserProfile.toUserEntity(): UserEntity {
    return UserEntity(
        gender            = this.gender,
        age               = this.age,
        weight            = this.weight.toFloatOrNull() ?: 0f,  // Fix #2: safe parse
        heightFeet        = this.heightFeet,
        heightInches      = this.heightInches,
        activityLevel     = this.activityLevel,
        goal              = this.goal,
        goalPace          = this.goalPace,
        medicalConditions = this.medicalConditions.joinToString(","),  // List → CSV string
        pregnancyStatus   = this.pregnancyStatus
    )
}

fun UserEntity.toUserProfile(): UserProfile {
    return UserProfile(
        gender            = this.gender,
        age               = this.age,
        weight            = this.weight.toString(),
        heightFeet        = this.heightFeet,
        heightInches      = this.heightInches,
        activityLevel     = this.activityLevel,
        goal              = this.goal,
        goalPace          = this.goalPace,
        medicalConditions = if (this.medicalConditions.isBlank()) emptyList()
                            else this.medicalConditions.split(","),  // CSV → List
        pregnancyStatus   = this.pregnancyStatus
    )
}

fun DailyGoals.toGoalsEntity(currentUserId : Int = 0): GoalsEntity{
    return GoalsEntity(
        userId = currentUserId,
        calories = this.calories,
        carbs = this.carbs,
        protein = this.protein,
        fats = this.fats
    )
}

fun GoalsEntity.toDailyGoals(): DailyGoals{
    return DailyGoals(
        calories = this.calories,
        carbs = this.carbs,
        protein = this.protein,
        fats = this.fats
    )
}

fun ProductEntity.toDomainProduct(): Product {
    return Product(
        barcode = this.barcode,
        productName = this.productName,
        brand = this.brand,
        imageUrl = this.imageUrl,
        calories = this.calories,
        protein = this.protein,
        carbs = this.carbs,
        fat = this.fat,
        fiber = this.fiber,
        sugars = this.sugars,
        scannedAt = this.scannedAt,
        quantity = this.quantity
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        barcode = this.barcode,
        productName = this.productName,
        brand = this.brand,
        imageUrl = this.imageUrl,
        calories = this.calories,
        protein = this.protein,
        carbs = this.carbs,
        fat = this.fat,
        fiber = this.fiber,
        sugars = this.sugars,
        scannedAt = this.scannedAt,
        quantity = this.quantity
    )
}

// ScannedProductEntity mappers

fun ScannedProductEntity.toDomainProduct(): Product {
    return Product(
        barcode = this.barcode,
        productName = this.productName,
        brand = this.brand,
        imageUrl = this.imageUrl,
        calories = this.calories,
        protein = this.protein,
        carbs = this.carbs,
        fat = this.fat,
        fiber = this.fiber,
        sugars = this.sugars,
        scannedAt = this.scannedAt
    )
}

fun ScannedProductEntity.toProductEntity(): ProductEntity {
    return ProductEntity(
        barcode = this.barcode,
        productName = this.productName,
        brand = this.brand,
        imageUrl = this.imageUrl,
        calories = this.calories,
        protein = this.protein,
        carbs = this.carbs,
        fat = this.fat,
        fiber = this.fiber,
        sugars = this.sugars,
        scannedAt = this.scannedAt
    )
}

fun Product.toScannedEntity(): ScannedProductEntity {
    return ScannedProductEntity(
        barcode = this.barcode,
        productName = this.productName,
        brand = this.brand,
        imageUrl = this.imageUrl,
        calories = this.calories,
        protein = this.protein,
        carbs = this.carbs,
        fat = this.fat,
        fiber = this.fiber,
        sugars = this.sugars,
        scannedAt = this.scannedAt
    )
}
