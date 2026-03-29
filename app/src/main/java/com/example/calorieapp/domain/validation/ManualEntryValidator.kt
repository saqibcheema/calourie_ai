package com.example.calorieapp.domain.validation

object ManualEntryValidator {

    /**
     * Validates the natural-language meal description that the user types.
     * This replaces the old validate(mealName, quantity, isGrams) API.
     */
    fun validateDescription(description: String, mealType: String = ""): ValidationResult {
        val trimmed = description.trim().replace(Regex("\\s+"), " ")

        // 1. Not empty
        if (trimmed.isBlank()) {
            return ValidationResult.Error("Please describe what you ate.")
        }

        // 2. Minimum length — need enough detail for AI to work with
        if (trimmed.length < 3) {
            return ValidationResult.Error("Please add more detail (at least 3 characters).")
        }

        // 3. Maximum length
        if (trimmed.length > 500) {
            return ValidationResult.Error("Description is too long (max 500 characters).")
        }

        // 4. Anti-injection: Block characters typically used in XSS or SQL injection
        val dangerousCharsRegex = Regex("[<>{}=;\\\\$%#^*]")
        if (trimmed.contains(dangerousCharsRegex)) {
            return ValidationResult.Error("Description contains invalid characters.")
        }

        return ValidationResult.Success(
            description = trimmed,
            mealType = mealType.ifBlank { "Meal" }
        )
    }

    sealed class ValidationResult {
        data class Success(val description: String, val mealType: String) : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }
}
