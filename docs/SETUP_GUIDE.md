# Setup Guide

This guide covers everything needed to build, configure, and run Calourie AI locally.

---

## Prerequisites

| Requirement | Version |
|---|---|
| **Android Studio** | Ladybug (2024.2) or newer |
| **JDK** | 11+ |
| **Android SDK** | API 36 (compile) / API 25 (min) |
| **Kotlin** | 2.0.21 |
| **Gradle** | 8.x (bundled with wrapper) |

---

## Clone & Open

```bash
git clone https://github.com/your-username/calourie-ai.git
cd calourie-ai
```

Open the project in Android Studio and let Gradle sync complete.

---

## Configuration

### API Key Setup

The app requires a **Groq API key** for AI-powered nutrition estimation.

1. **Get a Groq API key**: Sign up at [console.groq.com](https://console.groq.com)
2. **Add to `local.properties`**:

```properties
# local.properties (project root — gitignored)
GROQ_API_KEY=gsk_your_api_key_here
```

> [!IMPORTANT]
> The `local.properties` file is gitignored. Never commit API keys to version control.

The key is injected at build time via:
```kotlin
// build.gradle.kts
buildConfigField("String", "GROQ_API_KEY", "\"${properties.getProperty("GROQ_API_KEY") ?: ""}\"")
```

And accessed in code as `BuildConfig.GROQ_API_KEY`.

---

## Build & Run

### Development

```bash
# Using Gradle wrapper
./gradlew assembleDebug

# Or press ▶️ Run in Android Studio
```

### Run on Device/Emulator

- **Physical Device**: Connect via USB, enable USB Debugging
- **Emulator**: Use API 25+ system image
- **Camera features**: Barcode scanning requires a device with a camera (physical device recommended)

---

## Project Structure Quick Reference

```text
calourie-ai/
├── app/
│   ├── build.gradle.kts      # Dependencies + build config
│   ├── proguard-rules.pro    # ProGuard config (minify disabled)
│   └── src/
│       ├── main/java/com/example/calorieapp/
│       │   ├── Core/          # Navigation, routes
│       │   ├── DI/            # Hilt modules
│       │   ├── data/          # DataSources, models, repos
│       │   ├── domain/        # Entities, use cases, validation
│       │   ├── presentation/  # Screens, ViewModels, components
│       │   ├── ui/theme/      # Material 3 theming
│       │   └── util/          # Connectivity utils
│       ├── androidTest/       # Instrumented tests
│       └── test/              # Unit tests
├── docs/                      # Documentation (you are here)
├── gradle/                    # Gradle wrapper
├── build.gradle.kts           # Root build file
├── settings.gradle.kts        # Project settings
└── local.properties           # API keys (gitignored)
```

---

## Key Dependencies

| Library | Version | Purpose |
|---|---|---|
| Jetpack Compose BOM | Platform-managed | UI framework |
| Material 3 | Latest BOM | Design system |
| Navigation Compose | 2.9.7 | Type-safe navigation |
| Dagger Hilt | 2.51.1 | Dependency injection |
| Room | 2.6.0 | Local database |
| Retrofit | 2.9.0 | HTTP client |
| OkHttp Logging | 4.12.0 | Network debugging |
| ML Kit Barcode | 17.2.0 | Barcode scanning |
| CameraX | 1.3.0 | Camera preview |
| Coil | 2.5.0 | Image loading |
| Kotlinx Serialization | 1.7.3 | JSON / route serialization |
| Core Splashscreen | 1.0.1 | Splash screen API |

---

## Troubleshooting

### Common Issues

| Issue | Solution |
|---|---|
| **"No internet connection" on emulator** | Check emulator has internet access; try cold-booting the emulator |
| **AI estimation always fails** | Verify `GROQ_API_KEY` is set correctly in `local.properties`; rebuild the project |
| **Camera not working on emulator** | Use a physical device or create an emulator with camera enabled |
| **Room migration error** | Clean build (`./gradlew clean`) and reinstall the app |
| **Gradle sync fails** | Ensure JDK 11+ is configured in Android Studio; check internet connection |
| **Build fails with kapt errors** | Try `./gradlew clean assembleDebug`; ensure Hilt dependencies match |

### Verifying the Setup

After building and running:
1. **First launch** → should show Onboarding flow
2. Complete all 5 onboarding steps → should navigate to Dashboard
3. Tap "Manual Entry" → type a meal → should show AI estimation
4. Tap "Scan" → point at a barcode → should fetch product info
