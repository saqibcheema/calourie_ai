package com.example.calorieapp.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ManualEntryValidatorTest {

    // ── Happy path ──────────────────────────────────────────────────────────

    @Test
    fun `valid single-item description passes`() {
        val result = ManualEntryValidator.validateDescription("2 slice pizza", "Lunch")
        assertTrue(result is ManualEntryValidator.ValidationResult.Success)
        val success = result as ManualEntryValidator.ValidationResult.Success
        assertEquals("2 slice pizza", success.description)
        assertEquals("Lunch", success.mealType)
    }

    @Test
    fun `valid multi-item description passes`() {
        val result = ManualEntryValidator.validateDescription(
            "2 roti with 2 fried eggs and 1 medium cup tea with buffalo milk",
            "Breakfast"
        )
        assertTrue(result is ManualEntryValidator.ValidationResult.Success)
        val success = result as ManualEntryValidator.ValidationResult.Success
        assertEquals("2 roti with 2 fried eggs and 1 medium cup tea with buffalo milk", success.description)
    }

    @Test
    fun `valid complex combo meal passes`() {
        val result = ManualEntryValidator.validateDescription(
            "2 slice pizza, 1 zinger burger, 2 glass coke, some bites of pasta"
        )
        assertTrue(result is ManualEntryValidator.ValidationResult.Success)
    }

    @Test
    fun `description without quantities passes`() {
        val result = ManualEntryValidator.validateDescription("biryani")
        assertTrue(result is ManualEntryValidator.ValidationResult.Success)
    }

    @Test
    fun `meal type defaults to Meal when blank`() {
        val result = ManualEntryValidator.validateDescription("apple")
        assertTrue(result is ManualEntryValidator.ValidationResult.Success)
        assertEquals("Meal", (result as ManualEntryValidator.ValidationResult.Success).mealType)
    }

    @Test
    fun `whitespace is trimmed and normalized`() {
        val result = ManualEntryValidator.validateDescription("  2 roti   with   eggs  ", "Breakfast")
        assertTrue(result is ManualEntryValidator.ValidationResult.Success)
        assertEquals("2 roti with eggs", (result as ManualEntryValidator.ValidationResult.Success).description)
    }

    // ── Validation failures ─────────────────────────────────────────────────

    @Test
    fun `empty description fails`() {
        val result = ManualEntryValidator.validateDescription("   ")
        assertTrue(result is ManualEntryValidator.ValidationResult.Error)
        assertEquals("Please describe what you ate.", (result as ManualEntryValidator.ValidationResult.Error).message)
    }

    @Test
    fun `too short description fails`() {
        val result = ManualEntryValidator.validateDescription("ab")
        assertTrue(result is ManualEntryValidator.ValidationResult.Error)
        assertEquals("Please add more detail (at least 3 characters).", (result as ManualEntryValidator.ValidationResult.Error).message)
    }

    @Test
    fun `too long description fails`() {
        val longText = "a".repeat(501)
        val result = ManualEntryValidator.validateDescription(longText)
        assertTrue(result is ManualEntryValidator.ValidationResult.Error)
        assertEquals("Description is too long (max 500 characters).", (result as ManualEntryValidator.ValidationResult.Error).message)
    }

    @Test
    fun `description at exactly 500 chars passes`() {
        val text = "a".repeat(500)
        val result = ManualEntryValidator.validateDescription(text)
        assertTrue(result is ManualEntryValidator.ValidationResult.Success)
    }

    @Test
    fun `malicious characters in description fail`() {
        val result = ManualEntryValidator.validateDescription("<script>alert()</script>")
        assertTrue(result is ManualEntryValidator.ValidationResult.Error)
        assertEquals("Description contains invalid characters.", (result as ManualEntryValidator.ValidationResult.Error).message)
    }

    @Test
    fun `SQL injection attempt fails`() {
        val result = ManualEntryValidator.validateDescription("food'; DROP TABLE meals;--")
        assertTrue(result is ManualEntryValidator.ValidationResult.Error)
    }

    // ── Edge cases ──────────────────────────────────────────────────────────

    @Test
    fun `description with parentheses and commas passes`() {
        val result = ManualEntryValidator.validateDescription("chai (medium), 2 samosa")
        assertTrue(result is ManualEntryValidator.ValidationResult.Success)
    }

    @Test
    fun `description with numbers and units passes`() {
        val result = ManualEntryValidator.validateDescription("250g chicken breast grilled")
        assertTrue(result is ManualEntryValidator.ValidationResult.Success)
    }

    @Test
    fun `description with emoji passes`() {
        val result = ManualEntryValidator.validateDescription("🍕 2 slices pepperoni pizza")
        assertTrue(result is ManualEntryValidator.ValidationResult.Success)
    }
}
