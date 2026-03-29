# Architecture Overview

Calourie AI follows **Clean Architecture** with a strict layered dependency model. Data flows inward: the **Presentation** layer depends on **Domain**, and the **Data** layer implements **Domain** interfaces — but the Domain layer has zero knowledge of either.

---

## Technology Stack

| Category | Technology |
|---|---|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose (100% Declarative) |
| **Architecture** | Clean Architecture + MVVM + MVI-lite |
| **DI** | Dagger Hilt |
| **Database** | Room (SQLite) |
| **Networking** | Retrofit + OkHttp + Gson |
| **Barcode Scanning** | ML Kit + CameraX |
| **AI Nutrition** | Groq API (LLaMA 3.3 70B) |
| **Image Loading** | Coil |
| **Navigation** | Compose Navigation (type-safe) |
| **Min SDK** | API 25 (Android 7.1) |
| **Target SDK** | API 36 |

---

## Layer Diagram

```mermaid
graph TB
    subgraph Presentation["🎨 Presentation Layer"]
        direction LR
        Screens["Compose Screens<br/>Dashboard · ManualEntry · Onboarding"]
        VMs["ViewModels<br/>DashboardVM · ScanVM · ManualEntryVM<br/>OnBoardingVM · MainVM"]
        Components["UI Components<br/>BarcodeScannerView · NutritionSummary<br/>WheelPicker · CalorieCard · MacroCard"]
    end

    subgraph Domain["⚡ Domain Layer"]
        direction LR
        UseCases["Use Cases (12)<br/>AddMeal · DeleteMeal · ScanProduct<br/>EstimateNutrition · CalculateGoals · …"]
        Entities["Entities<br/>Product · UserProfile<br/>DailyGoals · DailyMacrosSummary"]
        RepoInterfaces["Repository Interfaces<br/>BarcodeRepository<br/>UserRepository<br/>GroqNutritionRepository"]
        Validation["Validation<br/>ManualEntryValidator"]
    end

    subgraph Data["💾 Data Layer"]
        direction LR
        RepoImpls["Implementations<br/>BarcodeRepositoryImpl<br/>UserRepositoryImpl<br/>GroqNutritionRepositoryImpl"]
        Local["Room DB (v6)<br/>ProductDao · UserDao<br/>ScannedProductDao"]
        Remote["Remote APIs<br/>OpenFoodFacts (Retrofit)<br/>Groq AI (Retrofit)"]
        Network["Network Layer<br/>Interceptors · Exceptions<br/>ConnectivityObserver"]
    end

    Screens --> VMs
    VMs --> UseCases
    UseCases --> RepoInterfaces
    RepoInterfaces -.->|implemented by| RepoImpls
    RepoImpls --> Local
    RepoImpls --> Remote
    Remote --> Network
```

---

## Dependency Direction

```
Presentation  ──depends on──►  Domain  ◄──implements──  Data
```

- **Domain** is the innermost layer — pure Kotlin, no Android dependencies
- **Presentation** observes `StateFlow`s exposed by ViewModels
- **Data** provides concrete repository implementations injected via Hilt

---

## Package Structure

```text
app/src/main/java/com/example/calorieapp/
├── Core/                   # Navigation graph, route definitions
├── DI/                     # Hilt AppModule (singleton wiring)
├── data/
│   ├── DataSource/
│   │   ├── local/          # Room DB, DAOs, DateConverter
│   │   └── remote/         # Retrofit API services, DTOs
│   ├── Models/             # Room entities, Mapper extensions
│   ├── network/
│   │   └── interceptors/   # OkHttp interceptors, custom exceptions
│   └── repository/         # Repository implementations
├── domain/
│   ├── entities/           # Pure business objects
│   ├── repository/         # Repository interfaces (contracts)
│   ├── useCases/           # Business logic, calculations
│   └── validation/         # Input validation
├── presentation/
│   ├── components/         # Reusable Compose components
│   ├── pages/              # Screen composables
│   │   ├── DashboardPages/ # Dashboard sub-screens, ManualEntry
│   │   └── onBoradingPages/# Onboarding step screens
│   └── viewModel/          # ViewModels (state management)
├── ui/theme/               # Color, Type, Theme definitions
└── util/                   # Connectivity observer
```

---

## Core Workflows

### 1. Barcode Scanning Flow

```mermaid
sequenceDiagram
    participant User
    participant ScanScreen as DashboardScreen
    participant ScanVM as ScanViewModel
    participant ScanUC as ScanProductUseCase
    participant Repo as BarcodeRepositoryImpl
    participant Cache as ScannedProductDao
    participant API as OpenFoodFacts API
    participant AddUC as AddMealUseCase
    participant DB as ProductDao

    User->>ScanScreen: Tap "Scan" button
    ScanScreen->>ScanVM: startScanning()
    Note over ScanScreen: CameraX + ML Kit activate
    ScanScreen->>ScanVM: onBarcodeDetected(barcode)
    ScanVM->>ScanUC: invoke(barcode)
    ScanUC->>Repo: scanProduct(barcode)
    Repo->>Cache: getScannedProductByBarcode(barcode)
    alt Cache Hit
        Cache-->>Repo: ScannedProductEntity
        Repo-->>ScanUC: Result.success(Product)
    else Cache Miss
        Repo->>API: GET /api/v2/product/{barcode}.json
        API-->>Repo: ProductResponseDto
        Repo->>Cache: insertScannedProduct(entity)
        Repo-->>ScanUC: Result.success(Product)
    end
    ScanUC-->>ScanVM: Result<Product>
    ScanVM-->>ScanScreen: Update ScanState
    User->>ScanScreen: Tap "Add to Meal"
    ScanScreen->>ScanVM: addToMeal()
    ScanVM->>AddUC: invoke(product)
    AddUC->>Repo: addMealFromScan(product)
    Repo->>DB: insertProduct(entity)
```

### 2. AI Manual Entry Flow

```mermaid
sequenceDiagram
    participant User
    participant Screen as ManualEntryScreen
    participant VM as ManualEntryViewModel
    participant Val as ManualEntryValidator
    participant UC as EstimateNutritionUseCase
    participant Repo as GroqNutritionRepositoryImpl
    participant API as Groq AI API
    participant AddUC as AddMealUseCase
    participant DB as ProductDao

    User->>Screen: Type meal description
    User->>Screen: Tap "Log Food"
    Screen->>VM: logFood()
    VM->>Val: validateDescription(desc, mealType)
    alt Validation Error
        Val-->>VM: Error(message)
        VM-->>Screen: Show error
    else Validation Success
        Val-->>VM: Success(desc, mealType)
        VM->>UC: invoke(prompt)
        UC->>Repo: estimateNutrition(desc)
        Repo->>API: POST /openai/v1/chat/completions
        API-->>Repo: GroqChatResponse
        Repo-->>UC: Result<NutritionEstimate>

        alt Clarification Needed
            UC-->>VM: isClarificationNeeded=true
            VM-->>Screen: Show clarification questions
            User->>Screen: Answer questions
            Screen->>VM: submitClarifications()
            Note over VM: Re-run logFood() with enriched description
        else Direct Estimate
            UC-->>VM: NutritionEstimate
            VM->>AddUC: invoke(product)
            AddUC->>DB: insertProduct(entity)
            VM-->>Screen: Show results overlay
        end
    end
```

### 3. Onboarding & Goal Calculation Flow

```mermaid
sequenceDiagram
    participant User
    participant Screens as Onboarding Screens
    participant VM as OnBoardingViewModel
    participant UC as SaveUserAndCalculateGoalsUseCase
    participant Calc as CalculationUtils
    participant Repo as UserRepositoryImpl
    participant DB as UserDao

    User->>Screens: Enter gender, age, height, weight, activity, goal
    Screens->>VM: Collect all fields
    User->>Screens: Tap "Let's Go"
    Screens->>VM: saveUser(profile)
    VM->>UC: invoke(userProfile)
    UC->>Calc: calculateGoals(profile)
    Note over Calc: BMR (Mifflin-St Jeor) → TDEE → Macros
    Calc-->>UC: DailyGoals
    UC->>Repo: saveUser(profile, goals)
    Repo->>DB: saveUserAndGoal(userEntity, goalsEntity)
    VM-->>Screens: Navigate to Dashboard
```

---

## Navigation Graph

```mermaid
graph LR
    OnBoarding["Dest.OnBoarding<br/>Onboarding Flow"]
    Dashboard["Dest.Dashboard<br/>Main Dashboard"]
    ManualEntry["Dest.ManualEntry<br/>AI Meal Entry"]

    OnBoarding -->|"onNavigateToDashboard()"| Dashboard
    Dashboard -->|"onNavigateToManualEntry()"| ManualEntry
    ManualEntry -->|"onBackClick()"| Dashboard
```

**Start destination** is determined at runtime by `MainViewModel.checkUserSession()`:
- If user exists → `Dest.Dashboard`
- If no user → `Dest.OnBoarding`
