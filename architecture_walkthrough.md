# Complete App Architecture Analysis Walkthrough

Calourie AI is built using a modern Android tech stack utilizing **Clean Architecture** combined with the **Model-View-ViewModel (MVVM)** pattern. This ensures that the app is scalable, testable, and maintainable.

## 1. Layers Overview

The application is strictly divided into three layers:

- **1. Presentation Layer (UI & ViewModels)**
  The UI is built entirely with **Jetpack Compose**. The state is managed by **ViewModels** which collect data from the Domain layer and expose it as `StateFlow` to the UI components.
  - **Key Screens**: [DashboardScreen](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardScreen.kt#31-237), [MealLoggedScreen](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/MealLoggedScreen.kt#23-226), `UserFormScreen`, `StartScreen`
  - **ViewModels**: [DashboardViewModel](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/viewModel/DashboardViewModel.kt#19-49), [ScanViewModel](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/viewModel/ScanViewModel.kt#15-79), `onBoardingViewModel`, `MainViewModel`

- **2. Domain Layer (Business Logic)**
  This is the core of the app. It does not know anything about Android Frameworks, UI, or databases. It only contains the business rules.
  - **UseCases**: Encapsulate a single task (e.g., [AddMealUseCase](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/useCases/AddMealUseCase.kt#7-14), `SaveUserAndCalculateGoalsUseCase`, [GetMealsByDateUseCase](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/useCases/GetMealsByDateUseCase.kt#8-15)).
  - **Interfaces**: Defines the contracts for repositories ([BarcodeRepository](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/repository/BarcodeRepository.kt#7-16), [UserRepository](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/DI/AppModule.kt#46-53)).
  - **Entities**: Pure Kotlin data classes ([Product](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/entities/Product.kt#5-24), [DailyGoals](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/Models/Mappers.kt#41-49)).

- **3. Data Layer**
  Responsible for fetching, saving, and caching data. It implements the interfaces defined in the Domain layer.
  - **Repositories**: [BarcodeRepositoryImpl](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/repository/BarcodeRepositoryImpl.kt#17-91), `UserRepositoryImpl` coordinate between data sources.
  - **Local Source**: **Room Database** ([AppDatabase](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/DataSource/local/AppDatabase.kt#11-18), [ProductDao](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/DataSource/local/ProductDao.kt#11-43), [UserDao](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/DI/AppModule.kt#40-45), [ScannedProductDao](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/DataSource/local/ScannedProductDao.kt#9-18)) stores [ScannedProductEntity](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/Models/ScannedProductEntity.kt#8-25) and [ProductEntity](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/Models/ProductEntity.kt#8-26).
  - **Remote Source**: **Retrofit** (`BarcodeApiService`) fetches barcode products from *OpenFoodFacts*.

---

## 2. Complete Interaction Diagram

The diagram below shows exactly how the code interacts with each other. It maps the two primary workflows: **1) Onboarding/User Profile Setup**, and **2) Scanning & Logging Meals**.

```mermaid
flowchart TD
    %% Define Styles
    classDef ui fill:#4CAF50,stroke:#388E3C,color:white;
    classDef viewmodel fill:#2196F3,stroke:#1976D2,color:white;
    classDef usecase fill:#FFA000,stroke:#FF8F00,color:white;
    classDef repo fill:#9C27B0,stroke:#7B1FA2,color:white;
    classDef local fill:#607D8B,stroke:#455A64,color:white;
    classDef remote fill:#F44336,stroke:#D32F2F,color:white;

    subgraph Presentation Layer
        UI_Onboarding["UserFormScreen/StartScreen"]:::ui
        VM_Onboarding["onBoardingViewModel"]:::viewmodel

        UI_Dashboard["DashboardScreen/MealLoggedScreen"]:::ui
        VM_Dashboard["DashboardViewModel"]:::viewmodel
        VM_Scan["ScanViewModel"]:::viewmodel
        
        UI_Onboarding <--> VM_Onboarding
        UI_Dashboard <--> VM_Dashboard
        UI_Dashboard <--> VM_Scan
    end

    subgraph Domain Layer
        UC_SaveUser["SaveUserAndCalculateGoalsUseCase"]:::usecase
        UC_GetGoals["GetGoalsUseCase"]:::usecase
        
        UC_ScanLog["ScanProductUseCase / AddMealUseCase"]:::usecase
        UC_Dashboard["GetMealsByDateUseCase / GetTodayNutrimentsSummaryUseCase"]:::usecase
        
        Repo_User{"UserRepository (Interface)"}:::usecase
        Repo_Barcode{"BarcodeRepository (Interface)"}:::usecase
    end

    subgraph Data Layer
        RepoImpl_User["UserRepositoryImpl"]:::repo
        RepoImpl_Barcode["BarcodeRepositoryImpl"]:::repo
        
        DAO_User[("UserDao (Room)")]:::local
        DAO_Product[("ProductDao (Room)")]:::local
        DAO_Cache[("ScannedProductDao (Cache)")]:::local
        
        API_Barcode(("BarcodeApiService (Retrofit/OpenFoodFacts)")):::remote
    end

    %% Flow: Onboarding
    VM_Onboarding -->|"User Details"| UC_SaveUser
    UC_SaveUser --> Repo_User
    Repo_User -.-> RepoImpl_User
    RepoImpl_User --> DAO_User

    %% Flow: Dashboard Fetching Data
    VM_Dashboard -->|"Observe Goals"| UC_GetGoals
    UC_GetGoals --> Repo_User
    
    VM_Dashboard -->|"Observe Macros & Meals"| UC_Dashboard
    UC_Dashboard --> Repo_Barcode
    Repo_Barcode -.-> RepoImpl_Barcode
    RepoImpl_Barcode --> DAO_Product

    %% Flow: Scanning a Product
    VM_Scan -->|"Barcode"| UC_ScanLog
    UC_ScanLog --> Repo_Barcode
    
    RepoImpl_Barcode -->|"1. Check Cache"| DAO_Cache
    RepoImpl_Barcode -->|"2. Wait! Not in Cache?"| API_Barcode
    API_Barcode -->|"Return JSON"| RepoImpl_Barcode
    RepoImpl_Barcode -->|"3. Save to Cache"| DAO_Cache

    %% Flow: Adding to Meal Log
    VM_Scan -->|"Add to Meal"| UC_ScanLog
    RepoImpl_Barcode -->|"Save Entity"| DAO_Product
```

---

## 3. Step-by-Step Data Flow Example

To understand how the layers connect, let's trace what happens when you **Scan a Barcode and Add it to a Meal**:

1. **User taps Scanner on [DashboardScreen](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardScreen.kt#31-237)**: The camera opens via CameraX/MLKit. A barcode string is detected.
2. **[ScanViewModel](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/viewModel/ScanViewModel.kt#15-79)**: Receives the string and calls [ScanProductUseCase](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/useCases/ScanProductUseCase.kt#7-19).
3. **[ScanProductUseCase](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/useCases/ScanProductUseCase.kt#7-19)**: Calls `BarcodeRepository.scanProduct(barcode)`.
4. **[BarcodeRepositoryImpl](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/repository/BarcodeRepositoryImpl.kt#17-91)**:
   - First, checks [ScannedProductDao](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/DataSource/local/ScannedProductDao.kt#9-18) (Cache table) to see if it was scanned before.
   - If not found, calls `BarcodeApiService.getProductByBarcode` using Retrofit to get data from OpenFoodFacts.
   - Saves the fresh data into the Cache table via `ScannedProductDao.insertScannedProduct(product)`.
   - Returns a pure [Product](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/entities/Product.kt#5-24) domain object back up the chain.
5. **[MealLoggedScreen](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/pages/DashboardPages/MealLoggedScreen.kt#23-226)**: The UI updates to show the product details with an **"Add to Meal"** button.
6. **User taps "Add to Meal"**: 
   - [ScanViewModel](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/viewModel/ScanViewModel.kt#15-79) fires [AddMealUseCase(product)](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/useCases/AddMealUseCase.kt#7-14).
   - [BarcodeRepositoryImpl](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/repository/BarcodeRepositoryImpl.kt#17-91) is told to permanently log the item.
   - It converts the [Product](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/entities/Product.kt#5-24) back into a [ProductEntity](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/Models/ProductEntity.kt#8-26) and saves it into the Meal Log table via `ProductDao.insertProduct(entity)`.
7. **[DashboardViewModel](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/presentation/viewModel/DashboardViewModel.kt#19-49) Reacts**: Because it collects `getMealsByDateUseCase` and `getTodayNutrimentsSummaryUseCase` as *StateFlows* directly from Room, the Dashboard UI instantly highlights the new calories and displays the meal item dynamically!
