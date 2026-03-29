# Network & Security Documentation

This document covers all network interceptors, connectivity monitoring, custom exceptions, and error handling patterns used throughout Calourie AI.

---

## OkHttp Interceptor Chain

Each Retrofit service is configured with a chain of interceptors that run **in order** on every HTTP request:

### BarcodeApiService Chain
```
Request → NetworkConnectionInterceptor → RateLimitInterceptor → OpenFoodFacts API
```

### GroqApiService Chain
```
Request → NetworkConnectionInterceptor → RateLimitInterceptor → GroqAuthInterceptor → HttpLoggingInterceptor → Groq API
```

---

## Interceptors

### NetworkConnectionInterceptor
> **File**: [NetworkConnectionInterceptor.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/network/interceptors/NetworkConnectionInterceptor.kt)

**Purpose**: Proactively checks internet connectivity before every API call, preventing requests from being sent when offline.

**How it works**:
1. Uses Android's `ConnectivityManager` to check for active network capabilities
2. Checks for Wi-Fi, Cellular, or Ethernet transport
3. If no valid transport is available → throws `NoConnectivityException`
4. If connected → proceeds with the request chain

```kotlin
class NetworkConnectionInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isConnected()) throw NoConnectivityException()
        return chain.proceed(chain.request())
    }
}
```

---

### RateLimitInterceptor
> **File**: [RateLimitInterceptor.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/network/interceptors/RateLimitInterceptor.kt)

**Purpose**: Client-side rate limiting to prevent API abuse and protect against accidental rapid-fire requests.

**Configuration**:
- **Default cooldown**: `2000ms` (2 seconds between requests)
- Uses `AtomicLong` for thread-safe timestamp tracking

**How it works**:
1. Records timestamp of each successful request
2. If a new request arrives within the cooldown window → throws `RateLimitException`
3. Otherwise → updates timestamp and proceeds

```kotlin
class RateLimitInterceptor(private val cooldownMs: Long = 2000L) : Interceptor {
    private val lastRequestTime = AtomicLong(0L)

    override fun intercept(chain: Interceptor.Chain): Response {
        val now = System.currentTimeMillis()
        if (now - lastRequestTime.get() < cooldownMs) throw RateLimitException()
        lastRequestTime.set(now)
        return chain.proceed(chain.request())
    }
}
```

---

### GroqAuthInterceptor
> **File**: [GroqAuthInterceptor.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/network/interceptors/GroqAuthInterceptor.kt)

**Purpose**: Injects the Groq API key into every request header.

**How it works**:
- Adds `Authorization: Bearer {apiKey}` to every outgoing request
- API key sourced from `BuildConfig.GROQ_API_KEY` (set via `local.properties`)

```kotlin
class GroqAuthInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request().newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .build()
        return chain.proceed(req)
    }
}
```

> **Security Note**: The API key is stored in `local.properties` (gitignored) and injected at build time via `buildConfigField`. It is **never** hardcoded in source files.

---

## Custom Exceptions
> **File**: [Exceptions.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/data/network/interceptors/Exceptions.kt)

| Exception | Message | Thrown By |
|---|---|---|
| `NoConnectivityException` | "No internet connection available" | `NetworkConnectionInterceptor` |
| `RateLimitException` | "You are making requests too quickly. Please wait a moment." | `RateLimitInterceptor` |

Both extend `java.io.IOException` so they are caught by Retrofit's standard error handling.

---

## Reactive Connectivity Monitoring

### ConnectivityObserver Interface
> **File**: [ConnectivityObserver.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/util/ConnectivityObserver.kt)

```kotlin
interface ConnectivityObserver {
    fun observe(): Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}
```

### NetworkConnectivityObserver Implementation
> **File**: [NetworkConnectivityObserver.kt](file:///e:/Desktop/calourie_ai/app/src/main/java/com/example/calorieapp/util/NetworkConnectivityObserver.kt)

Uses Android's `ConnectivityManager.NetworkCallback` to emit real-time connectivity status changes as a Kotlin `callbackFlow`.

**Status mapping**:
| Callback | Status |
|---|---|
| `onAvailable()` | `Available` |
| `onUnavailable()` | `Unavailable` |
| `onLosing()` | `Losing` |
| `onLost()` | `Lost` |

**Injected via Hilt** and consumed by:
- `ScanViewModel` — blocks scanning/adding when offline, shows error messages
- `ConnectivityStatus` composable — displays an offline banner in the UI

---

## Error Handling Patterns

### ViewModel Error Mapping

ViewModels catch specific exception types and map them to user-friendly messages:

```kotlin
// In ManualEntryViewModel
val errorMsg = when (errorException) {
    is NoConnectivityException ->
        "No internet connection. Please turn on Wi-Fi or mobile data."
    is RateLimitException ->
        "You're querying too fast! Please slow down and try again in a few seconds."
    else ->
        "AI Estimation failed: ${errorException?.message ?: "Unknown AI error"}"
}
```

### Defense in Depth

The app implements **two layers** of connectivity protection:

| Layer | Component | Scope |
|---|---|---|
| **Proactive (UI)** | `ConnectivityObserver` + `ScanViewModel` | Blocks user actions before API call |
| **Reactive (Network)** | `NetworkConnectionInterceptor` | Catches connectivity loss during a request |

This ensures users never see raw network errors — they always receive clear, actionable messages.

---

## Input Validation Security

The `ManualEntryValidator` blocks potentially dangerous characters in user input before it reaches the AI API:

```kotlin
// Characters blocked: < > { } = ; \ $ % # ^ *
val dangerousCharsRegex = Regex("[<>{}=;\\\\$%#^*]")
```

This provides defense against:
- **Prompt injection** — preventing users from manipulating the AI system prompt
- **XSS/HTML injection** — blocking script tags in text fields
- **SQL injection** — blocking SQL metacharacters (defense in depth alongside Room's parameterized queries)
