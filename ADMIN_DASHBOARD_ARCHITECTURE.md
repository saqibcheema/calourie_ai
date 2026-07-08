# Calourie AI — Admin Dashboard & Firebase Backend Architecture

> **Purpose**: This document defines everything needed to build a web-based admin dashboard backed by Firebase, so the app owner can change API keys, AI models, feature flags, and view analytics — all **without** a Play Store update.

---

## 1. High-Level Architecture

```
┌─────────────────────────────────────────────────────┐
│                  ADMIN (You)                        │
│         Web Dashboard (React/Next.js)               │
│    hosted on Firebase Hosting                       │
└──────────┬──────────────────────┬───────────────────┘
           │                      │
     Firebase Auth          Firebase Admin SDK
     (Email login)          (via Cloud Functions)
           │                      │
           ▼                      ▼
┌──────────────────────────────────────────────────────┐
│                FIREBASE BACKEND                      │
│                                                      │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────┐ │
│  │ Remote Config│  │  Firestore   │  │  Cloud     │ │
│  │              │  │              │  │  Functions │ │
│  │ • API Keys   │  │ • Analytics  │  │            │ │
│  │ • Model Names│  │   events     │  │ • Secure   │ │
│  │ • Feature    │  │ • App usage  │  │   key      │ │
│  │   Flags      │  │   stats      │  │   proxy    │ │
│  │ • Maint.Mode │  │              │  │ • Stats    │ │
│  └──────────────┘  └──────────────┘  │   aggreg.  │ │
│                                      └────────────┘ │
└──────────────────────┬───────────────────────────────┘
                       │
                       ▼
            ┌─────────────────────┐
            │   Android App       │
            │   (Calourie AI)     │
            │                     │
            │ Fetches config on   │
            │ app start, uses     │
            │ remote keys instead │
            │ of BuildConfig      │
            └─────────────────────┘
```

---

## 2. Firebase Services Required

| Service | Purpose | Free Tier |
|---------|---------|-----------|
| **Firebase Auth** | Admin login to dashboard (email/password) | 10K auth/month ✅ |
| **Remote Config** | Store API keys, model names, feature flags, maintenance mode | Unlimited ✅ |
| **Firestore** | Store analytics events from app, aggregated stats | 50K reads/day ✅ |
| **Cloud Functions** | Secure API key proxy, stats aggregation | 2M invocations/month ✅ |
| **Firebase Hosting** | Host the web dashboard | 10GB transfer/month ✅ |
| **Google Analytics** | Auto user analytics (DAU, sessions, retention) | Unlimited ✅ |

---

## 3. Firebase Project Setup (Step-by-Step)

### Step 1: Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create Project" → Name: `calourie-ai`
3. Enable Google Analytics → Select default account

### Step 2: Register Android App
1. Project Settings → Add App → Android
2. Package name: `com.example.calorieapp`
3. Download `google-services.json` → Place at `app/google-services.json`

### Step 3: Enable Services

| Service | Path in Console |
|---------|----------------|
| Authentication | Build → Authentication → Enable "Email/Password" |
| Firestore | Build → Firestore → Create Database → Production mode → `asia-south1` |
| Remote Config | Build → Remote Config → Create Configuration |
| Hosting | Build → Hosting → Get Started |
| Functions | Build → Functions → Get Started |

### Step 4: Create Admin User
In Authentication → Users → Add User with your email/password. **Note the UID.**

### Step 5: Android Dependencies (Phase 2 only)

```kotlin
// app/build.gradle.kts — add these
dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
}
```

---

## 4. Firestore Database Schema

```
firestore/
├── app_analytics/                     # Daily aggregated stats
│   └── {YYYY-MM-DD}/
│       ├── totalScans: number
│       ├── totalAiVisionUses: number
│       ├── totalManualEntries: number
│       ├── totalMealsLogged: number
│       ├── uniqueDevices: number
│       └── updatedAt: timestamp
│
├── app_events/                        # Raw events from app
│   └── {auto-id}/
│       ├── type: "scan" | "ai_vision" | "manual_entry" | "app_open"
│       ├── deviceId: string
│       ├── timestamp: timestamp
│       ├── metadata: map
│       └── appVersion: string
│
└── admin_config/                      # Dashboard state
    └── dashboard_state/
        ├── lastKeyRotation: timestamp
        └── lastConfigPublish: timestamp
```

---

## 5. Remote Config Keys (All 14)

### API Keys

| Key | Type | Default |
|-----|------|---------|
| `gemini_api_key` | String | Current key from local.properties |
| `groq_api_key` | String | Current key from local.properties |

### AI Model Config

| Key | Type | Default |
|-----|------|---------|
| `gemini_model_name` | String | `gemini-2.0-flash` |
| `groq_model_name` | String | `llama-3.3-70b-versatile` |
| `groq_temperature` | Number | `0.3` |
| `groq_max_tokens` | Number | `500` |

### Feature Flags

| Key | Type | Default |
|-----|------|---------|
| `feature_ai_vision_enabled` | Boolean | `true` |
| `feature_barcode_scanner_enabled` | Boolean | `true` |
| `feature_manual_entry_enabled` | Boolean | `true` |
| `feature_statistics_enabled` | Boolean | `true` |

### App Control

| Key | Type | Default |
|-----|------|---------|
| `maintenance_mode` | Boolean | `false` |
| `maintenance_message` | String | `App is under maintenance.` |
| `minimum_app_version` | Number | `1` |
| `force_update_message` | String | `Please update to the latest version.` |

---

## 6. Cloud Functions

| Function | Trigger | Purpose |
|----------|---------|---------|
| `aggregateDailyStats` | Scheduled (daily) | Summarize app_events → app_analytics |
| `getDashboardStats` | HTTPS callable | Return stats for dashboard |
| `getRealtimeStats` | HTTPS callable | Return today's live stats |

---

## 7. Web Dashboard Structure

### Tech Stack: Next.js 14 + Firebase JS SDK + Tailwind CSS + Recharts

### Pages

| Page | Purpose |
|------|---------|
| `/login` | Admin email/password login |
| `/dashboard` | Overview with stats cards + 7-day trend chart |
| `/dashboard/api-keys` | Edit Gemini & Groq API keys → publish to Remote Config |
| `/dashboard/ai-models` | Change model names, temperature, max tokens |
| `/dashboard/feature-flags` | Toggle AI Vision / Scanner / Manual Entry / Stats on/off |
| `/dashboard/maintenance` | Toggle maintenance mode, set message, force update version |
| `/dashboard/analytics` | Charts: daily meals, feature usage breakdown, active devices |

### Dashboard Overview Cards

| Card | Source |
|------|--------|
| Total Meals Today | `app_analytics/{today}.totalMealsLogged` |
| AI Vision Uses | `app_analytics/{today}.totalAiVisionUses` |
| Barcode Scans | `app_analytics/{today}.totalScans` |
| Active Devices | `app_analytics/{today}.uniqueDevices` |

### Project Structure

```
dashboard/
├── app/
│   ├── layout.tsx          # Root layout with auth guard
│   ├── login/page.tsx      # Login page
│   └── dashboard/
│       ├── layout.tsx      # Sidebar + header
│       ├── page.tsx        # Overview
│       ├── api-keys/page.tsx
│       ├── ai-models/page.tsx
│       ├── feature-flags/page.tsx
│       ├── maintenance/page.tsx
│       └── analytics/page.tsx
├── components/
│   ├── Sidebar.tsx
│   ├── StatsCard.tsx
│   ├── FeatureToggle.tsx
│   └── AnalyticsChart.tsx
├── lib/
│   ├── firebase.ts         # Client Firebase init
│   ├── firebase-admin.ts   # Admin SDK (server-side)
│   └── auth.ts             # Auth hooks
└── firebase.json
```

---

## 8. Android App Integration (Phase 2)

### Files That Change

| File | Change |
|------|--------|
| `app/build.gradle.kts` | Add Firebase deps |
| `DI/AppModule.kt` | Wire `RemoteConfigRepository` |
| `GeminiVisionService.kt` | Read key + model from Remote Config |
| `GroqModels.kt` | Read model, temperature, maxTokens remotely |
| `GroqNutritionRepositoryImpl.kt` | Read API key remotely |
| `GroqAuthInterceptor.kt` | Accept dynamic key |
| `MainScreen.kt` | Check feature flags + maintenance |
| **NEW** `RemoteConfigRepository.kt` | Wrapper for Firebase Remote Config |
| **NEW** `MaintenanceScreen.kt` | Shown when maintenance_mode = true |
| **NEW** `AnalyticsRepository.kt` | Log events to Firestore |

### Hardcoded Values Migration Table

| Value | Current File | Remote Config Key |
|-------|-------------|-------------------|
| Gemini API key | `BuildConfig.GEMINI_API_KEY` | `gemini_api_key` |
| Groq API key | `BuildConfig.GROQ_API_KEY` | `groq_api_key` |
| `"gemini-2.0-flash"` | `GeminiVisionService.kt:19` | `gemini_model_name` |
| `"llama-3.3-70b-versatile"` | `GroqModels.kt:7` | `groq_model_name` |
| `0.3` temperature | `GroqModels.kt:11` | `groq_temperature` |
| `500` max tokens | `GroqModels.kt:13` | `groq_max_tokens` |

---

## 9. Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /admin_config/{doc} {
      allow read, write: if request.auth != null
                         && request.auth.uid == 'YOUR_ADMIN_UID';
    }
    match /app_events/{event} {
      allow create: if true;
      allow read: if request.auth != null
                  && request.auth.uid == 'YOUR_ADMIN_UID';
      allow update, delete: if false;
    }
    match /app_analytics/{day} {
      allow read: if request.auth != null
                  && request.auth.uid == 'YOUR_ADMIN_UID';
      allow write: if false;
    }
    match /{document=**} {
      allow read, write: if false;
    }
  }
}
```

---

## 10. Deployment Checklist

### Phase 1: Dashboard (Do First — No app changes)

- [ ] Create Firebase project
- [ ] Enable Auth, Firestore, Remote Config, Hosting, Analytics
- [ ] Create admin user, note UID
- [ ] Set up all 14 Remote Config keys
- [ ] Deploy Firestore security rules
- [ ] Build Next.js dashboard with all 6 pages
- [ ] Implement server-side API routes for Remote Config Admin SDK
- [ ] Deploy to Firebase Hosting
- [ ] Deploy Cloud Functions
- [ ] Test: login → change key → verify publish

### Phase 2: Android App Integration (After dashboard works)

- [ ] Add `google-services.json` to `app/`
- [ ] Add Firebase dependencies
- [ ] Create `RemoteConfigRepository.kt`
- [ ] Create `AnalyticsRepository.kt`
- [ ] Modify 6 existing files (see Section 8)
- [ ] Create `MaintenanceScreen.kt`
- [ ] Test on device: change key in dashboard → app picks it up
- [ ] Publish final APK to Play Store
