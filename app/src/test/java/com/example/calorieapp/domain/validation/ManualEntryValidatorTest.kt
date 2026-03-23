package com.example.calorieapp.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ManualEntryValidatorTest {

    @Test
    fun `valid meal name and valid grams quantity passes`() {
        val result = ManualEntryValidator.validate("Apple", "100", true, "Red apple")
        assertTrue("Expected Success for valid input", result is ManualEntryValidator.ValidationResult.Success)
        val success = result as ManualEntryValidator.ValidationResult.Success
        assertEquals("Apple", success.mealName)
        assertEquals("100", success.quantity)
    }

    @Test
    fun `valid meal name and valid portion string passes`() {
        val result = ManualEntryValidator.validate("Pizza", "2 slices", false, "")
        assertTrue("Expected Success for valid portion", result is ManualEntryValidator.ValidationResult.Success)
        val success = result as ManualEntryValidator.ValidationResult.Success
        assertEquals("Pizza", success.mealName)
        assertEquals("2 slices", success.quantity)
    }

    @Test
    fun `empty meal name fails`() {
        val result = ManualEntryValidator.validate("   ", "100", true)
        assertTrue(result is ManualEntryValidator.ValidationResult.Error)
        assertEquals("Meal name cannot be empty.", (result as ManualEntryValidator.ValidationResult.Error).message)
    }

    @Test
    fun `short meal name fails`() {
        val result = ManualEntryValidator.validate("A", "100", true)
        assertTrue(result is ManualEntryValidator.ValidationResult.Error)
        assertEquals("Meal name must be at least 2 characters.", (result as ManualEntryValidator.ValidationResult.Error).message)
    }

    @Test
    fun `empty quantity fails`() {
        val result = ManualEntryValidator.validate("Apple", "   ", false)
        assertTrue(result is ManualEntryValidator.ValidationResult.Error)
        assertEquals("Quantity cannot be empty.", (result as ManualEntryValidator.ValidationResult.Error).message)
    }

    @Test
    fun `non-numeric grams quantity fails`() {
        val result = ManualEntryValidator.validate("Apple", "abc", true)
        assertTrue(result is ManualEntryValidator.ValidationResult.Error)
        assertEquals("Quantity must be a valid number when Grams is selected.", (result as ManualEntryValidator.ValidationResult.Error).message)
    }

    @Test
    fun `zero or negative grams quantity fails`() {
        val result = ManualEntryValidator.validate("Apple", "-5", true)
        assertTrue(result is ManualEntryValidator.ValidationResult.Error)
        assertEquals("Grams must be greater than 0.", (result as ManualEntryValidator.ValidationResult.Error).message)
        
        val resultZero = ManualEntryValidator.validate("Apple", "0", true)
        assertTrue(resultZero is ManualEntryValidator.ValidationResult.Error)
        assertEquals("Grams must be greater than 0.", (resultZero as ManualEntryValidator.ValidationResult.Error).message)
    }

    @Test
    fun `malicious characters in fields fail`() {
        val result = ManualEntryValidator.validate("<script>alert()</script>", "100", true)
        assertTrue(result is ManualEntryValidator.ValidationResult.Error)
        assertEquals("Input contains invalid characters (<, >, {, }, =, ;).", (result as ManualEntryValidator.ValidationResult.Error).message)
    }
}
