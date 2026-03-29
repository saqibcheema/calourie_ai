# Domain Layer Documentation

The Domain layer is the heart of the application. It contains pure business logic with no Android framework dependencies, ensuring high testability and portability.

---

## Entities

All entities live in `domain/entities/` and represent core business objects.

### Product
> **File**: [Product.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/entities/Product.kt)

The central data class for both scanned and manually entered meals.

```kotlin
data class Product(
    val barcode: String,
    val productName: String?,
    val brand: String?,
    val imageUrl: String?,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double?,
    val sugars: Double?,
    val scannedAt: Date = Date(),
    val quantity: Int = 1
)
```

**Computed Properties**:
| Property | Logic | Description |
|---|---|---|
| `isValid` | `calories >= 0 && protein >= 0 && carbs >= 0 && fat >= 0` | Guards against negative nutrition values |
| `totalMacros` | `(protein + carbs + fat) * quantity` | Total macronutrient grams (quantity-adjusted) |

> **Note**: For manual entries, `barcode` is set to `"manual_{timestamp}"` to ensure uniqueness.

---

### UserProfile
> **File**: [UserProfile.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/entities/UserProfile.kt)

```kotlin
data class UserProfile(
    val gender: String,       // "Male" or "Female"
    val age: Int,
    val weight: String,       // In kg (stored as String for display flexibility)
    val heightFeet: Int,
    val heightInches: Int,
    val activityLevel: String, // "No Exercise", "Low Activity", "Moderate Activity", "High Activity"
    val goal: String           // "Maintain", "Lose Weight", "Gain Weight"
)
```

---

### DailyGoals
> **File**: [DailyGoals.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/entities/DailyGoals.kt)

```kotlin
data class DailyGoals(
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fats: Int
)
```

Calculated during onboarding by `CalculationUtils` and persisted via `UserRepository`.

---

### DailyMacrosSummary
> **File**: [DailyMacrosSummary.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/entities/DailyMacrosSummary.kt)

```kotlin
data class DailyMacrosSummary(
    val totalCalories: Double?,
    val totalProtein: Double?,
    val totalFats: Double?,
    val totalCarbs: Double?
)
```

Returned by the `ProductDao.getTodayTotalMacros()` query — an aggregated `SUM` of all non-deleted meals for a given date.

---

## Use Cases

All use cases live in `domain/useCases/` and follow the single-responsibility principle. Each use case wraps exactly one business operation.

### Meal Management

| Use Case | File | Dependency | Description |
|---|---|---|---|
| **AddMealUseCase** | [AddMealUseCase.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/useCases/AddMealUseCase.kt) | `BarcodeRepository` | Adds a meal (manual or scanned) to the database |
| **DeleteMealUseCase** | [DeleteMealUseCase.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/useCases/DeleteMealUseCase.kt) | `BarcodeRepository` | Soft-deletes a meal by barcode |
| **UpdateMealQuantityUseCase** | [UpdateMealQuantityUseCase.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/useCases/UpdateMealQuantityUseCase.kt) | `BarcodeRepository` | Updates the quantity of a logged meal |
| **GetMealsByDateUseCase** | [GetMealsByDateUseCase.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/useCases/GetMealsByDateUseCase.kt) | `BarcodeRepository` | Returns meals for a specific date as a Flow |

### Scanning

| Use Case | File | Dependency | Description |
|---|---|---|---|
| **ScanProductUseCase** | [ScanProductUseCase.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/useCases/ScanProductUseCase.kt) | `BarcodeRepository` | Scans a barcode and returns the product (cache-first) |
| **GetScanHistoryUseCase** | [GetScanHistoryUseCase.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/useCases/GetScanHistoryUseCase.kt) | `BarcodeRepository` | Returns all scan history as a reactive Flow |

### AI Nutrition Estimation

| Use Case | File | Dependency | Description |
|---|---|---|---|
| **EstimateNutritionUseCase** | [EstimateNutritionUseCase.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/useCases/EstimateNutritionUseCase.kt) | `GroqNutritionRepository` | Sends a food description to Groq AI, returns a `NutritionEstimate` |

### User & Goals

| Use Case | File | Dependency | Description |
|---|---|---|---|
| **SaveUserAndCalculateGoalsUseCase** | [SaveUserAndCalculateGoalsUseCase.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/useCases/SaveUserAndCalculateGoalsUseCase.kt) | `UserRepository` | Calculates goals via `CalculationUtils`, then persists both profile and goals |
| **CheckUserSessionUseCase** | [CheckUserSessionUseCase.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/useCases/CheckUserSessionUseCase.kt) | `UserRepository` | Checks if a user profile exists (for navigation routing) |
| **GetGoalsUseCase** | [GetGoalsUseCase.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/useCases/GetGoalsUseCase.kt) | `UserRepository` | Returns daily goals as a reactive Flow |

### Dashboard

| Use Case | File | Dependency | Description |
|---|---|---|---|
| **GetTodayNutrimentsSummaryUseCase** | [GetTodayNutrimentsSummaryUseCase.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/useCases/GetTodayNutrimentsSummaryUseCase.kt) | `BarcodeRepository` | Returns aggregated macros for a given date |

---

## CalculationUtils

> **File**: [CalculationUtils.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/useCases/CalculationUtils.kt)

A singleton utility that implements the **Mifflin-St Jeor** equation for BMR calculation and derives daily macro targets.

### Algorithm

```
1. Convert height to cm:  height_cm = ((feet × 12) + inches) × 2.54
2. Calculate BMR:
   BMR = (weight_kg × 10) + (height_cm × 6.25) - (age × 5) + gender_offset
   - Male: gender_offset = +5
   - Female: gender_offset = -161

3. Calculate TDEE:
   TDEE = BMR × activity_multiplier
   - No Exercise:       1.2
   - Low Activity:      1.375
   - Moderate Activity: 1.55
   - High Activity:     1.725

4. Apply goal modifier:
   - Maintain:    target = TDEE
   - Lose Weight: target = TDEE - 500
   - Gain Weight: target = TDEE + 500

5. Calculate macros:
   - Protein: weight_kg × 2 (g)
   - Fat:     weight_kg × 0.9 (g)
   - Carbs:   (target_calories - protein_cals - fat_cals) / 4 (g)
```

---

## Validation

### ManualEntryValidator

> **File**: [ManualEntryValidator.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/validation/ManualEntryValidator.kt)

A singleton validator for natural-language meal descriptions before they're sent to the AI.

**Validation Rules**:

| Rule | Check | Error Message |
|---|---|---|
| Non-empty | `trimmed.isBlank()` | "Please describe what you ate." |
| Min length | `length < 3` | "Please add more detail (at least 3 characters)." |
| Max length | `length > 500` | "Description is too long (max 500 characters)." |
| Anti-injection | Contains `< > { } = ; \ $ % # ^ *` | "Description contains invalid characters." |

**Result Type**:
```kotlin
sealed class ValidationResult {
    data class Success(val description: String, val mealType: String) : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}
```

The `Success` result normalizes the description by collapsing multiple whitespace characters into single spaces.
