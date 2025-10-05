# Repository Guidelines

## Project Structure & Module Organization
- Core: `core/domain`, `core/data/db-sqldelight` (default), optional `core/data/db-room`, `core/presentation`, `core/test`.
- UI: `ui/shared` (common Compose code and resources), `ui/test-jvm` (JVM Compose UI test utilities).
- Apps: `app/android`, `app/desktop`, `app/ios-kit` (CocoaPods framework), `app/iosApp` (Xcode project).
- Tooling: `build-logic` (Gradle conventions), `thirdparty` (vendored modules), `gradle/libs.versions.toml` (versions).
- Switch DB module via `gradle.properties` key `CORE_DATA_DB_MODULE`.

## Build, Test, and Development Commands
- Build all: `./gradlew build` (compiles all modules and runs unit tests).
- Android app: `./gradlew :app:android:assembleDebug` (APK), `:installDebug` (to device).
- Android instrumentation tests: `./gradlew :app:android:connectedAndroidTest` (requires emulator/device; uses AndroidX Test Orchestrator).
- Desktop app: `./gradlew :app:desktop:run` (launches JVM desktop Compose app).
- iOS: `cd iosApp && pod install` then open `iosApp/iosApp.xcworkspace` in Xcode and run. Regenerate podspec if needed: `./gradlew :app:ios-kit:podspec`.

## Coding Style & Naming Conventions
- Kotlin official style (`kotlin.code.style=official`); 4-space indentation; organize imports.
- Packages lowercase dot-separated; classes/objects `PascalCase`; functions/vars `camelCase`; constants `UPPER_SNAKE_CASE`.
- One public top-level type per file; filename matches the type.
- Compose: prefer state hoisting; keep @Composable functions small and previewable.

## Testing Guidelines
- Unit tests: Kotlin Multiplatform `kotlin("test")`/JUnit. Run all with `./gradlew test` or per module (e.g., `:ui:shared:jvmTest`).
- Android UI tests: Espresso/Compose in `app/android/src/androidTest`. Run with `connectedAndroidTest`.
- Desktop UI tests: in `app/desktop/src/jvmTest` using `uiTestJUnit4`.
- Name tests `FooBarTest.kt`; prefer Given_When_Then method names.

## Commit & Pull Request Guidelines
- Use imperative, concise titles (<= 72 chars). Example: `core/domain: Add paging for notes`.
- Describe rationale and impact; link issues (e.g., `Closes #123`).
- For UI changes, add screenshots/GIFs; list affected modules.
- Before opening a PR: run `./gradlew build` locally and ensure no Android Lint/security lint regressions.

## Security & Configuration Tips
- Never commit secrets or real keystores; keep `keystore.properties` and `local.properties` local.
- `google-services.json` should contain non-sensitive config only; sanitize before sharing.
- Keep SDK/JDK versions in sync with `libs.versions.toml`; avoid editing CI-only settings locally.
