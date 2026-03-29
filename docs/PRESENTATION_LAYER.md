# Presentation Layer Documentation

The Presentation layer is built entirely with **Jetpack Compose** and follows the **MVVM + MVI-lite** pattern. ViewModels expose `StateFlow`s that Compose screens observe reactively.

---

## Navigation

### Route Definitions
> **File**: [approutes.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/Core/approutes.kt)

```kotlin
sealed interface Dest {
    @Serializable data object OnBoarding : Dest
    @Serializable data object Dashboard : Dest
    @Serializable data object ManualEntry : Dest
}
```

Uses **Kotlin Serialization** with Compose Navigation's type-safe API.

### Navigation Graph
> **File**: [CalorieNavigation.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/Core/CalorieNavigation.kt)

| Route | Screen | Navigation |
|---|---|---|
| `Dest.OnBoarding` | `OnBoardingScreen` | → Dashboard (clears back stack) |
| `Dest.Dashboard` | `DashboardScreen` | → ManualEntry |
| `Dest.ManualEntry` | `ManualEntryScreen` | ← Back to Dashboard |

**Start destination** is dynamically set by `MainViewModel` based on whether a user session exists.

---

## ViewModels

### MainViewModel
> **File**: [MainViewModel.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/viewModel/MainViewModel.kt)

**Purpose**: Determines the app's start destination.

| State | Type | Description |
|---|---|---|
| `startDestination` | `StateFlow<Dest?>` | `Dest.Dashboard` if user exists, `Dest.OnBoarding` otherwise |

**Dependencies**: `CheckUserSessionUseCase`

---

### DashboardViewModel
> **File**: [DashboardViewModel.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/viewModel/DashboardViewModel.kt)

**Purpose**: Manages the main dashboard state — daily goals, macros summary, meal list, and date selection.

| State | Type | Description |
|---|---|---|
| `dailyGoals` | `StateFlow<DailyGoals?>` | User's target calories and macros |
| `selectedDate` | `StateFlow<LocalDate>` | Currently selected date (defaults to today) |
| `dailySummary` | `StateFlow<DailyMacrosSummary?>` | Aggregated nutrition for selected date |
| `dailyMeals` | `StateFlow<List<Product>>` | Meals logged on selected date |

**Key Pattern**: Uses `flatMapLatest` on `selectedDate` to reactively re-query meals and summary when the date changes.

| Action | Method | Description |
|---|---|---|
| Change date | `updateSelectedDate(date)` | Updates `_selectedDate`, triggers re-query |
| Increase qty | `increaseQuantity(product)` | Calls `UpdateMealQuantityUseCase` with `qty + 1` |
| Decrease/delete | `decreaseQuantityOrDelete(product)` | Decreases qty; if `qty == 1`, soft-deletes |

**Dependencies**: `GetGoalsUseCase`, `GetTodayNutrimentsSummaryUseCase`, `GetMealsByDateUseCase`, `UpdateMealQuantityUseCase`, `DeleteMealUseCase`

---

### ScanViewModel
> **File**: [ScanViewModel.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/viewModel/ScanViewModel.kt)

**Purpose**: Manages barcode scanning workflow with connectivity awareness.

```kotlin
data class ScanState(
    val isScanning: Boolean = false,
    val isLoading: Boolean = false,
    val scannedProduct: Product? = null,
    val isAddedToMeal: Boolean = false,
    val error: String? = null,
    val isOffline: Boolean = false
)
```

| Action | Method | Description |
|---|---|---|
| Start scan | `startScanning()` | Activates camera/ML Kit |
| Stop scan | `stopScanning()` | Deactivates scanner |
| Barcode found | `onBarcodeDetected(barcode)` | Checks connectivity, then calls `ScanProductUseCase` |
| Add to meal | `addToMeal()` | Calls `AddMealUseCase` with scanned product |
| Clear | `clearProduct()` | Resets product state |

**Connectivity**: Observes `ConnectivityObserver` in `init {}` and blocks scanning/adding when offline.

**Dependencies**: `ScanProductUseCase`, `AddMealUseCase`, `ConnectivityObserver`

---

### ManualEntryViewModel
> **File**: [ManualEntryViewModel.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/viewModel/ManualEntryViewModel.kt)

**Purpose**: Manages the AI-powered manual meal entry flow with multi-step clarification.

```kotlin
data class ManualEntryState(
    // User input
    val mealDescription: String,
    val mealType: String,          // Breakfast, Lunch, Dinner, Snack
    val eatingContext: String,     // "", "Restaurant", "Home Cooked", "Street Food"

    // Clarification flow
    val isClarificationNeeded: Boolean,
    val clarificationQuestions: List<ClarificationQuestion>,
    val clarificationAnswers: Map<String, String>,

    // Loading / result state
    val isLoading: Boolean,
    val isEstimating: Boolean,
    val nutritionConfidence: String,
    val estimatedCalories/Protein/Carbs/Fat/Fiber/Sugars: Double,
    val itemizedBreakdown: List<FoodItemEstimate>,
    val showResults: Boolean,
    val loggedFoodName: String,
    val errorMessage: String?,
    val isSuccess: Boolean
)
```

**Clarification Flow**:
1. User types description → `logFood()`
2. Validator checks input → calls Groq AI
3. If AI returns `isClarificationNeeded = true`:
   - Show clarification questions with predefined options
   - User answers → `submitClarifications()`
   - Answers appended to description → re-run `logFood()`
4. If direct estimate → save to DB → show results overlay

**Error Handling**: Maps specific exceptions to user-friendly messages:
- `NoConnectivityException` → "No internet connection…"
- `RateLimitException` → "You're querying too fast…"

**Dependencies**: `AddMealUseCase`, `EstimateNutritionUseCase`

---

### OnBoardingViewModel
> **File**: [onBoardingViewModel.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/viewModel/onBoardingViewModel.kt)

**Purpose**: Collects user profile data across 5 onboarding screens, then saves profile and goals.

**Dependencies**: `SaveUserAndCalculateGoalsUseCase`

---

## Screens

### Dashboard Screen
> **File**: [DashboardScreen.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardScreen.kt)

Main app screen showing:
- Calorie progress ring card
- Macro breakdown (protein, carbs, fat) progress bars
- Date selector row (horizontal scroll)
- Meal history list with swipe-to-delete
- Bottom action buttons (Scan, Manual Entry)

### Manual Entry Screen
> **File**: [ManualEntryScreen.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/ManualEntryScreen.kt)

AI-powered meal logging form:
- Meal type selector (Breakfast/Lunch/Dinner/Snack)
- Eating context chips (Restaurant/Home Cooked/Street Food)
- Natural language description text field
- AI results overlay with itemized breakdown

### Onboarding Screens
> **Directory**: `presentation/pages/onBoradingPages/`

5-step flow collecting user profile data:

| Screen | File | Collects |
|---|---|---|
| Gender | [genderScreen.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/onBoradingPages/genderScreen.kt) | Male / Female |
| Age | [AgeScreen.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/onBoradingPages/AgeScreen.kt) | Age (wheel picker) |
| Height & Weight | [height&weight.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/onBoradingPages/height&weight.kt) | Feet, inches, kg |
| Activity Level | [activityLevel.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/onBoradingPages/activityLevel.kt) | No Exercise → High Activity |
| Goal | [goalScreen.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/onBoradingPages/goalScreen.kt) | Maintain / Lose / Gain |

---

## Reusable Components

> **Directory**: `presentation/components/`

| Component | File | Description |
|---|---|---|
| **BarcodeScannerView** | [BarcodeScannerView.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/components/BarcodeScannerView.kt) | CameraX preview with ML Kit barcode analysis |
| **BarcodeAnalyser** | [BarcodeAnalayser.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/components/BarcodeAnalayser.kt) | ML Kit `ImageAnalysis.Analyzer` implementation |
| **NutritionSummary** | [NutritionSummary.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/components/NutritionSummary.kt) | Nutrition sheet content (macros, add-to-meal) |
| **WheelPicker** | [WheelPicker.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/components/WheelPicker.kt) | Custom scrollable wheel picker (age, height) |
| **CustomButtons** | [CustomButtons.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/components/CustomButtons.kt) | Styled action buttons |
| **ConnectivityStatus** | [ConnectivityStatus.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/components/ConnectivityStatus.kt) | Offline banner composable |

### Dashboard Sub-Components

> **Directory**: `presentation/pages/DashboardPages/`

| Component | File | Description |
|---|---|---|
| **CalorieCard** | [CalorieCard.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/CalorieCard.kt) | Circular progress ring for calorie tracking |
| **MacroCard** | [MacroCard.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/MacroCard.kt) | Individual macro nutrient card |
| **MacroRow** | [MacroRow.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/MacroRow.kt) | Horizontal row of macro cards |
| **DateSelectedRow** | [DateSelectedRow.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/DateSelectedRow.kt) | Horizontal date selector |
| **MealItemRow** | [MealItemRow.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/MealItemRow.kt) | Meal list item with swipe-to-delete |
| **MealLoggedScreen** | [MealLoggedScreen.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/MealLoggedScreen.kt) | Success confirmation after logging |
| **NutritionSheetContent** | [NutritionSheetContent.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/NutritionSheetContent.kt) | Bottom sheet with nutrition details |
| **TopHeader** | [TopHeader.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/TopHeader.kt) | App top bar |
| **LogOptionCard** | [logOptionCard.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/logOptionCard.kt) | Scan / Manual entry action cards |
| **RecentUploadPlaceholder** | [RecentUploadPlaceholder.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/RecentUploadPlaceholder.kt) | Placeholder when no meals logged |

### Manual Entry Sub-Components

> **Directory**: `presentation/pages/DashboardPages/ManualEntry/components/`

| Component | File | Description |
|---|---|---|
| **ManualEntryCommon** | [ManualEntryCommon.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/ManualEntry/components/ManualEntryCommon.kt) | Shared UI elements: SectionCard, SegmentedChip, DescriptionSection, etc. |
| **AiResultsOverlay** | [AiResultsOverlay.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/ManualEntry/components/AiResultsOverlay.kt) | Full-screen overlay showing AI estimation results with itemized breakdown |
