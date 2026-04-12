# Presentation Layer Documentation

The Presentation layer is built entirely with **Jetpack Compose** and follows the **MVVM + MVI-lite** pattern. ViewModels expose `StateFlow`s that Compose screens observe reactively.

---

## Navigation

### Route Definitions
> **File**: [approutes.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/Core/approutes.kt)

```kotlin
sealed interface Dest {
    @Serializable data object OnBoarding : Dest
    @Serializable data object MainScreen : Dest
    @Serializable data object ManualEntry : Dest
}
```

Uses **Kotlin Serialization** with Compose Navigation's type-safe API.

### Top-Level Navigation Graph
> **File**: [CalorieNavigation.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/Core/CalorieNavigation.kt)

| Route | Screen | Navigation |
|---|---|---|
| `Dest.OnBoarding` | `OnBoardingScreen` | → MainScreen (clears back stack) |
| `Dest.MainScreen` | `MainScreen` | → ManualEntry |
| `Dest.ManualEntry` | `ManualEntryScreen` | ← Back to MainScreen |

**Start destination** is dynamically set by `MainViewModel` based on whether a user session exists.

### Inner Tab Navigation (inside MainScreen)
> **File**: [MainScreen.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/MainScreen.kt)

`MainScreen` hosts its own `NavHost` with a **floating bottom dock** (pill-shaped nav bar + FAB):

| Route | Screen | Visible in Bottom Bar |
|---|---|---|
| `dashboard_route` | `DashboardScreen` | ✅ Home tab |
| `statistics_route` | `StatisticsScreen` | ✅ Stats tab |
| `profile_route` | `ProfileScreen` | ✅ Profile tab |
| `scanner_route` | `ScannerFeatureScreen` | ❌ (hides nav bar) |
| `aivision_route` | `AiVisionScreen` | ❌ (hides nav bar) |

The **FAB (+)** in the bottom dock opens a `ModalBottomSheet` (`NutritionSheetContent`) with three log options: Scan, AI Vision, and Manual Entry.

---

## ViewModels

### MainViewModel
> **File**: [MainViewModel.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/viewModel/MainViewModel.kt)

**Purpose**: Determines the app's start destination.

| State | Type | Description |
|---|---|---|
| `startDestination` | `StateFlow<Dest?>` | `Dest.MainScreen` if user exists, `Dest.OnBoarding` otherwise |

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

### StatisticsViewModel
> **File**: [StatisticsViewModel.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/viewModel/StatisticsViewModel.kt)

**Purpose**: Provides monthly performance data for the Statistics screen — heatmap, calorie balance chart, and macro consistency.

| State | Type | Description |
|---|---|---|
| `dailyGoals` | `StateFlow<DailyGoals?>` | Used as target reference for charts |
| `monthlyCalories` | `StateFlow<Map<String, Double>>` | `dateString → totalCalories` map for heatmap |
| `averageMonthlySummary` | `StateFlow<DailyMacrosSummary?>` | Average daily macros across active logging days |
| `todaySummary` | `StateFlow<DailyMacrosSummary?>` | Today's macro totals |

**Key Logic**: Queries `getMonthlyMacros(pattern)` for current month using `yyyy-MM-%` pattern. Calculates per-macro averages only for days with > 0 calories logged.

**Dependencies**: `UserRepository`, `BarcodeRepository` (direct)

---

### ProfileViewModel
> **File**: [ProfileViewModel.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/viewModel/ProfileViewModel.kt)

**Purpose**: Manages the Profile screen — loads existing user data and saves updated profile with recalculated goals.

| State | Type | Description |
|---|---|---|
| `age` | `Int` | Editable age |
| `weight` | `String` | Editable weight in kg |
| `feetForHeight` | `Int` | Height (feet component) |
| `inchesForHeight` | `Int` | Height (inches component) |
| `activityLevel` | `String` | Selected activity level |
| `goal` | `String` | Selected fitness goal |
| `isSaving` | `Boolean` | Loading state for save button |

**Dependencies**: `SaveUserAndCalculateGoalsUseCase`, `UserRepository`

---

### AiVisionViewModel
> **File**: [AiVisionViewModel.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/viewModel/AiVisionViewModel.kt)

**Purpose**: Manages the AI Vision camera flow — captures photo, sends to Groq for analysis, and returns nutrition estimates.

**Dependencies**: `AnalyzeFoodImageUseCase`, `AddMealUseCase`, `ConnectivityObserver`

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

### MainScreen (Root Host)
> **File**: [MainScreen.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/MainScreen.kt)

The root app shell. Contains its own `NavHost` with a **floating bottom dock** UI:
- Pill-shaped white nav bar with 3 animated tabs (Home, Stats, Profile)
- Dark circular FAB (+) that opens `NutritionSheetContent` bottom sheet
- Bottom bar auto-hides when navigating to `scanner_route` or `aivision_route`
- Animated slide transitions between tabs (left/right direction-aware)

**Inner Navigation Routes**: `dashboard_route`, `statistics_route`, `profile_route`, `scanner_route`, `aivision_route`

---

### Dashboard Screen
> **File**: [DashboardScreen.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardScreen.kt)

Main app screen showing:
- **Top header** with app logo and streak counter
- **Date selector row** (horizontal scroll, today highlighted)
- **Calorie card** with circular progress ring showing calories remaining
- **Macro cards** (Protein / Carbs / Fats remaining)
- **Meal history list** with quantity controls and swipe-to-delete
- Empty state placeholder when no meals logged

---

### Statistics Screen
> **File**: [StatisticsScreen.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/StatisticsScreen.kt)

Performance analytics screen with 3 animated sections:

| Section | Component | Description |
|---|---|---|
| **Monthly Streak** | `ConsistencyHeatmapSection` | GitHub-style calendar heatmap — cell shading shows calorie intake vs goal (4 intensity levels). Scrollable horizontally. |
| **Calorie Balance** | `CalorieBalanceSection` | 7-day animated bar chart. Red bars = over target, dark bars = within target. Y-axis auto-scales to 1.5× calorie goal. |
| **Monthly Goal Consistency** | `MacroConsistencySection` | Animated linear progress bars for Protein, Carbs, Fats vs monthly averages |

All cards use `AnimatedVisibility` with staggered `fadeIn + slideInVertically` on first render. Responds to `DailyGoals` as the reference target.

---

### Profile Screen
> **File**: [ProfileScreen.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/ProfileScreen.kt)

User profile management screen with 4 animated sections:

| Section | Component | Description |
|---|---|---|
| **Profile Header** | `ProfileHeader()` | Title + subtitle |
| **Physical Metrics** | `PhysicalStatsSection` | Age (yrs), Weight (kg), Height (ft + in) via `EditorialInputField` |
| **Activity Routine** | `ActivityLevelSection` | Radio-style `InteractiveRow` selector for No Exercise / Low / Moderate / High Activity |
| **Primary Objective** | `GoalSection` | 3-option pill selector: Lose Weight / Maintain / Gain Weight |

**Save button** (`SaveButtonSection`): Gradient button with spring-scale press animation, loading spinner, and success state ("Goals Synchronized" ✓). Haptic feedback on press and confirmation.

---

### Onboarding Screens
> **Directory**: `presentation/pages/onboardingPages/`

5-step flow collecting user profile data:

| Screen | File | Collects |
|---|---|---|
| Gender | [genderScreen.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/onboardingPages/genderScreen.kt) | Male / Female / Other |
| Age | [AgeScreen.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/onboardingPages/AgeScreen.kt) | Age via WheelPicker |
| Height & Weight | [height&weight.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/onboardingPages/height&weight.kt) | Feet, inches, kg |
| Activity Level | [activityLevel.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/onboardingPages/activityLevel.kt) | No Exercise → High Activity |
| Goal | [goalScreen.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/onboardingPages/goalScreen.kt) | Maintain / Lose Weight / Gain Weight |

Progress indicator at the top shows current step. "Continue" CTA advances to the next screen.

---

### Manual Entry Screen
> **File**: [ManualEntryScreen.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/ManualEntryScreen.kt)

AI-powered meal logging form:
- Meal type selector (Breakfast / Lunch / Dinner / Snack)
- Eating context chips (Restaurant / Home Cooked / Street Food)
- Natural language description text field
- AI results overlay (`AiResultsOverlay`) with itemized breakdown, confidence badge, and macro summary

---

### AI Vision Screen
> **File**: [AiVisionScreen.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/AiVision/AiVisionScreen.kt)

Camera-based food recognition:
- CameraX live preview with capture trigger
- Sends captured image to Groq AI for food analysis
- Returns nutrition estimates the same as Manual Entry flow
- Auto-hides bottom nav bar when active

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
| **MealItemRow** | [MealItemRow.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/MealItemRow.kt) | Meal list item with quantity controls |
| **MealLoggedScreen** | [MealLoggedScreen.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/MealLoggedScreen.kt) | Success confirmation after logging |
| **NutritionSheetContent** | [NutritionSheetContent.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/NutritionSheetContent.kt) | Bottom sheet with 3 log options (Scan / AI Vision / Manual) |
| **TopHeader** | [TopHeader.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/TopHeader.kt) | App top bar with logo and streak |
| **LogOptionCard** | [logOptionCard.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/logOptionCard.kt) | Scan / Manual / AI Vision action cards |
| **RecentUploadPlaceholder** | [RecentUploadPlaceholder.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/RecentUploadPlaceholder.kt) | Empty state when no meals logged |

### Statistics Screen Components

Built directly inside `StatisticsScreen.kt` (no separate files):

| Composable | Description |
|---|---|
| `HeaderSection()` | Title "Performance" + insights icon badge |
| `ConsistencyHeatmapSection()` | GitHub-style calendar grid with 4-shade calorie intensity |
| `CalorieBalanceSection()` | 7-day animated bar chart with y-axis labels |
| `MacroConsistencySection()` | Protein / Carbs / Fats progress with `MacroLinearProgress` |
| `MacroLinearProgress()` | Animated `LinearProgressIndicator` with label and gram values |

### Profile Screen Components

Built directly inside `ProfileScreen.kt`:

| Composable | Description |
|---|---|
| `ProfileHeader()` | "My Profile" title |
| `PhysicalStatsSection()` | 2×2 grid of `EditorialInputField` for physical metrics |
| `ActivityLevelSection()` | `InteractiveRow` list with icon + checkmark for activity selection |
| `GoalSection()` | 3-pill animated goal selector |
| `SaveButtonSection()` | Spring-animated gradient save button with haptic + success states |
| `SectionContainer()` | White card container with title for profile sections |
| `EditorialInputField()` | Uppercase-labeled number input field with unit suffix |
| `InteractiveRow()` | Bordered, animated selectable row for activity options |

### Manual Entry Sub-Components

> **Directory**: `presentation/pages/DashboardPages/ManualEntry/`

| Component | File | Description |
|---|---|---|
| **ManualEntryCommon** | `ManualEntryCommon.kt` | Shared UI: SectionCard, SegmentedChip, DescriptionSection |
| **AiResultsOverlay** | `AiResultsOverlay.kt` | Full-screen overlay with AI estimation results and itemized breakdown |
