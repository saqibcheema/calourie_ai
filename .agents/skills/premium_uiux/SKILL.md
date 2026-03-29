---
name: compose-uiux-animations
version: 2.0.0
description: "Universal Jetpack Compose UI/UX, Animations, and Performance Standard. Enforces modern M3 patterns and frictionless motion."
scope: "All Composable functions, UI layers, and Navigation logic."
---

# Jetpack Compose UI/UX & Animation Master Skill

## 0. DISCOVERY (Mandatory Startup)
Before writing UI, scan the project and identify:
1. **Tokens:** `Color.kt`, `Type.kt`, `Shape.kt`.
2. **Theme:** The specific `MaterialTheme` wrapper name.
3. **Environment:** Is Google Stitch MCP available? Is `DESIGN.md` present?
4. **Context:** Purpose of the component & existing animation easing constants.

## 1. GOOGLE STITCH MCP WORKFLOW
- **Call Stitch:** For new screens, off-brand UI fixes, or "Design DNA" extraction.
- **Ignore Stitch:** For logic fixes, performance tuning, or adding animations (Stitch is static).
- **Workflow:** Stitch Design -> Extract DNA -> Read DESIGN.md -> Implement Compose -> Verify.

## 2. VISUAL & RESPONSIVENESS PRINCIPLES
- **No Hardcoding:** Use `fillMaxWidth()`, `weight()`, `LocalConfiguration`. Never hardcode `dp` for screen sizes.
- **Hierarchy:** One dominant element per screen. Use contrast, not borders.
- **Insets:** Use `systemBarsPadding()`. Assume `enableEdgeToEdge()` is active in MainActivity.
- **Touch Targets:** Minimum 48dp x 48dp for all interactive elements.
- **Text:** Always use `TextOverflow.Ellipsis` and `maxLines` for dynamic data.

## 3. ANIMATION & MOTION STANDARDS
- **Purpose:** Animations must communicate state, guide attention, or provide spatial continuity.
- **Speeds:** - Fast (100-150ms): Feedback/Toggles.
    - Normal (250-350ms): Cards/Visibility.
    - Slow (400-600ms): Screen transitions.
- **Easing:** `FastOutSlowInEasing` for movement; `spring()` for physical micro-interactions.
- **Required Cases:** - `animateFloatAsState` for progress.
    - `AnimatedVisibility` for errors/expansions.
    - `SharedTransitionLayout` for hero animations between screens.
- **Lists:** Provide stable `key` to `items()`. Stagger entry animations by 30-60ms.

## 4. PERFORMANCE & STATE RULES
- **State Collection:** Always use `collectAsStateWithLifecycle()`.
- **Optimization:** Use `derivedStateOf` for multi-state computations. Wrap expensive logic/lambdas in `remember`.
- **Render Thread:** Use `graphicsLayer {}` for scale, alpha, and rotation to avoid layout passes.
- **Strong Skipping:** Respect Compose 1.5.4+ skipping rules; don't over-remember trivial lambdas.

## 5. MATERIAL 3 & ACCESSIBILITY
- **Base Components:** Use M3 defaults. Customise via parameters, not by wrapping in bare Boxes.
- **Haptics:** Use `LocalHapticFeedback` for deletions (strong), success (medium), and selection (light).
- **A11y:** Every icon needs `contentDescription`. Never use color as the only state indicator.

## 6. THE "NEVER" LIST (Hard Constraints)
- Never nest `verticalScroll` in `LazyColumn`.
- Never use `while(true)` loops for UI animations (use `InfiniteTransition`).
- Never let animations exceed 700ms.
- Never remove ripple effects from buttons/clickable surfaces.
- Never show a blank screen during loading (use Shimmer/Skeleton).

## 7. QUICK DECISION REFERENCE

| Goal | API / Tool |
| :--- | :--- |
| **New Screen** | Call Google Stitch MCP |
| **Hero/Morph Effect** | `SharedTransitionLayout` + `sharedElement()` |
| **Infinite Shimmer** | `rememberInfiniteTransition` + `LinearEasing` |
| **Layout-Safe Anim** | `Modifier.graphicsLayer { ... }` |
| **Swipe Action** | `SwipeToDismissBox` + Haptic Feedback |
| **Number Count-up** | `LaunchedEffect` loop @ 60fps |

---
**Internal Instruction:** Adhere to these principles strictly. If a request conflicts with these rules, notify the user before proceeding.