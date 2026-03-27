package com.example.calorieapp.domain.validation

object ManualEntryValidator {
    fun validate(mealName: String, quantity: String, isGrams: Boolean, description: String = ""): ValidationResult {
        val trimmedMealName = mealName.trim().replace(Regex("\\s+"), " ")
        val trimmedQuantity = quantity.trim()
        val trimmedDescription = description.trim()

        // 1. Meal Name Validations
        if (trimmedMealName.isBlank()) {
            return ValidationResult.Error("Meal name cannot be empty.")
        }
        if (trimmedMealName.length < 2) {
            return ValidationResult.Error("Meal name must be at least 2 characters.")
        }
        if (trimmedMealName.length > 50) { // Stricter limit for manual entry
            return ValidationResult.Error("Meal name is too long.")
        }

        // 2. Quantity Validations
        if (trimmedQuantity.isBlank()) {
            return ValidationResult.Error("Quantity or portion must be selected.")
        }

        if (isGrams) {
            val grams = trimmedQuantity.toIntOrNull()
            if (grams == null) {
                return ValidationResult.Error("Grams must be a whole number.")
            }
            if (grams <= 0) {
                return ValidationResult.Error("Grams must be greater than 0.")
            }
            if (grams > 2000) {
                return ValidationResult.Error("That's a lot of food! Please enter a realistic amount (max 2kg).")
            }
        } else {
            if (trimmedQuantity.length > 100) {
                return ValidationResult.Error("Portion description is too long.")
            }
        }

        // 3. Anti-injection: Block characters typically used in XSS or SQL injection
        val dangerousCharsRegex = Regex("[<>{}=;\\$%#^*]")
        if (trimmedMealName.contains(dangerousCharsRegex) ||
            trimmedQuantity.contains(dangerousCharsRegex) ||
            trimmedDescription.contains(dangerousCharsRegex)
        ) {
            return ValidationResult.Error("Input contains invalid characters.")
        }

        return ValidationResult.Success(trimmedMealName, trimmedQuantity, trimmedDescription)
    }

    sealed class ValidationResult {
        data class Success(val mealName: String, val quantity: String, val description: String) : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }
}
