# API Reference

This document covers all repository interfaces, remote API services, and data transfer objects (DTOs) used in Calourie AI.

---

## Repository Interfaces

All repository interfaces live in `domain/repository/` and define the contracts that the Data layer must implement.

### BarcodeRepository

> **File**: [BarcodeRepository.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/repository/BarcodeRepository.kt)

| Method | Signature | Description |
|---|---|---|
| `addMeal` | `suspend fun addMeal(meal: Product)` | Persist a manually entered meal to the local database |
| `addMealFromScan` | `suspend fun addMealFromScan(product: Product)` | Persist a barcode-scanned product. Handles duplicate barcodes by incrementing quantity |
| `scanProduct` | `suspend fun scanProduct(barcode: String): Result<Product>` | Lookup a barcode — checks local cache first, falls back to OpenFoodFacts API |
| `getScanHistory` | `fun getScanHistory(): Flow<List<Product>>` | Reactive stream of all logged meals (non-deleted), ordered by most recent |
| `getMealsByDate` | `fun getMealsByDate(selectedDate: String): Flow<List<Product>>` | Reactive stream of meals filtered by a specific date string (ISO format) |
| `updateMealQuantity` | `suspend fun updateMealQuantity(barcode: String, newQuantity: Int)` | Update the serving quantity for a specific product entry |
| `deleteProduct` | `suspend fun deleteProduct(barcode: String)` | Soft-delete a product by setting `isDeleted = true` |
| `getDailySummary` | `fun getDailySummary(selectedDate: String): Flow<DailyMacrosSummary?>` | Reactive stream returning aggregated macros (calories, protein, carbs, fats) for a given date |

---

### UserRepository

> **File**: [UserRepository.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/repository/UserRepository.kt)

| Method | Signature | Description |
|---|---|---|
| `saveUser` | `suspend fun saveUser(user: UserProfile, goals: DailyGoals)` | Persist user profile and calculated daily goals |
| `getUser` | `fun getUser(): Flow<UserProfile?>` | Reactive stream of the current user profile |
| `getGoals` | `fun getGoals(): Flow<DailyGoals?>` | Reactive stream of the user's daily macro goals |
| `checkUserSession` | `fun checkUserSession(): Flow<Boolean>` | Emit `true` if a user profile exists (used to decide navigation start) |

---

### GroqNutritionRepository

> **File**: [GroqNutritionRepository.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/domain/repository/GroqNutritionRepository.kt)

| Method | Signature | Description |
|---|---|---|
| `estimateNutrition` | `suspend fun estimateNutrition(foodDescription: String): Result<NutritionEstimate>` | Send a natural-language food description to Groq AI, receive a structured nutrition estimate or clarification questions |

---

## Remote API Services

### BarcodeApiService (OpenFoodFacts)

> **File**: [BarcodeApiService.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/DataSource/remote/BarcodeApiService.kt)
> **Base URL**: `https://world.openfoodfacts.org/`

| Method | HTTP | Endpoint | Description |
|---|---|---|---|
| `getProductByBarcode` | `GET` | `/api/v2/product/{barcode}.json` | Fetch product nutrition data by barcode from OpenFoodFacts |

**Response**: `ProductResponseDto` (contains `status: Int` and `product: ProductDto?`)

---

### GroqApiService (Groq AI)

> **File**: [GroqApiService.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/DataSource/remote/GroqApiService.kt)
> **Base URL**: `https://api.groq.com/`

| Method | HTTP | Endpoint | Description |
|---|---|---|---|
| `estimateNutrition` | `POST` | `/openai/v1/chat/completions` | Send a food description prompt to LLaMA 3.3 70B and receive a JSON nutrition estimate |

**Request Body**: `GroqChatRequest`
**Response**: `GroqChatResponse`

---

## Data Transfer Objects (DTOs)

### OpenFoodFacts DTOs

#### ProductResponseDto
> **File**: [ProductDto.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/DataSource/remote/dto/ProductDto.kt)

| Field | Type | JSON Key | Description |
|---|---|---|---|
| `status` | `Int` | `status` | API response status (1 = found) |
| `product` | `ProductDto?` | `product` | The product data, if found |

#### ProductDto

| Field | Type | JSON Key | Description |
|---|---|---|---|
| `productName` | `String?` | `product_name` | Product display name |
| `brand` | `String?` | `brands` | Brand name |
| `imageUrl` | `String?` | `image_url` | Product image URL |
| `nutriments` | `NutrimentsDto?` | `nutriments` | Nutritional data per 100g |
| `servingSize` | `String?` | `serving_size` | Serving size string |
| `productQuantity` | `Double?` | `product_quantity` | Total product quantity |
| `quantityPerUnitValue` | `Double?` | `quantity_per_unit_value` | Quantity per unit |

#### NutrimentsDto
> **File**: [NutrimentsDto.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/DataSource/remote/dto/NutrimentsDto.kt)

| Field | Type | JSON Key | Description |
|---|---|---|---|
| `calories` | `Double?` | `energy-kcal_100g` | Calories per 100g |
| `protein` | `Double?` | `proteins_100g` | Protein (g) per 100g |
| `carbs` | `Double?` | `carbohydrates_100g` | Carbohydrates (g) per 100g |
| `fat` | `Double?` | `fat_100g` | Fat (g) per 100g |
| `fiber` | `Double?` | `fiber_100g` | Fiber (g) per 100g |
| `sugars` | `Double?` | `sugars_100g` | Sugars (g) per 100g |

---

### Groq AI DTOs

> **File**: [GroqModels.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/DataSource/remote/dto/GroqModels.kt)

#### GroqChatRequest

| Field | Type | Default | Description |
|---|---|---|---|
| `model` | `String` | `"llama-3.3-70b-versatile"` | LLM model identifier |
| `messages` | `List<GroqMessage>` | — | Conversation messages (system + user) |
| `responseFormat` | `GroqResponseFormat` | `{"type": "json_object"}` | Force JSON output |
| `temperature` | `Double` | `0.3` | Low temperature for consistent estimates |
| `maxTokens` | `Int` | `500` | Max response length |

#### GroqMessage

| Field | Type | Description |
|---|---|---|
| `role` | `String` | `"system"` or `"user"` |
| `content` | `String` | Message content |

#### GroqChatResponse

| Field | Type | Description |
|---|---|---|
| `choices` | `List<GroqChoice>` | List of response choices |

#### NutritionEstimate (Parsed Result)

| Field | Type | Default | Description |
|---|---|---|---|
| `isClarificationNeeded` | `Boolean` | `false` | Whether AI needs more info from the user |
| `clarificationQuestions` | `List<ClarificationQuestion>` | `[]` | Follow-up questions for the user |
| `displayName` | `String` | `"Meal"` | AI-generated concise name (e.g., "Roti, Eggs & Tea") |
| `calories` | `Double` | `0.0` | Estimated total calories |
| `protein` | `Double` | `0.0` | Estimated protein (g) |
| `carbs` | `Double` | `0.0` | Estimated carbs (g) |
| `fat` | `Double` | `0.0` | Estimated fat (g) |
| `fiber` | `Double` | `0.0` | Estimated fiber (g) |
| `sugars` | `Double` | `0.0` | Estimated sugars (g) |
| `items` | `List<FoodItemEstimate>` | `[]` | Per-item macro breakdown |
| `confidence` | `String` | `"medium"` | `"high"`, `"medium"`, or `"low"` |

#### ClarificationQuestion

| Field | Type | Description |
|---|---|---|
| `question` | `String` | The clarification question text |
| `options` | `List<String>` | Predefined answer choices |

#### FoodItemEstimate

| Field | Type | Description |
|---|---|---|
| `name` | `String` | Individual food item name |
| `calories` | `Double` | Calories for this item |
| `protein` | `Double` | Protein (g) for this item |
| `carbs` | `Double` | Carbs (g) for this item |
| `fat` | `Double` | Fat (g) for this item |
