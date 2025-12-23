# NoteDelight Documentation

Welcome to the NoteDelight documentation! This directory contains comprehensive documentation for developers and AI agents working on this Kotlin Multiplatform project.

## Documentation Structure

### 📖 Main Documentation

- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Clean Architecture, MVVM, data flow, state management
- **[CONTRIBUTING.md](../CONTRIBUTING.md)** - Code style, development workflow, commit conventions
- **[TESTING_GUIDE.md](TESTING_GUIDE.md)** - Testing strategy, patterns, and tools
- **[AI_AGENT_GUIDE.md](AI_AGENT_GUIDE.md)** - AI agent quick reference and common tasks
- **[MANUAL_BUILD_INSTALL.md](MANUAL_BUILD_INSTALL.md)** - Step-by-step manual build and install instructions for all platforms

### 📦 Module Documentation

Each module has its own detailed README.md:

#### Core Modules
- **[core/domain/README.md](../core/domain/README.md)** - Business logic layer
- **[core/presentation/README.md](../core/presentation/README.md)** - Presentation layer (ViewModels)
- **[core/data/db-sqldelight/README.md](../core/data/db-sqldelight/README.md)** - SQLDelight data implementation
- **[core/data/db-room/README.md](../core/data/db-room/README.md)** - Room data implementation
- **[core/test/README.md](../core/test/README.md)** - Shared test utilities

#### UI Modules
- **[ui/shared/README.md](../ui/shared/README.md)** - Shared Compose UI
- **[ui/test/README.md](../ui/test/README.md)** - Multiplatform Compose UI tests
- **[ui/test-jvm/README.md](../ui/test-jvm/README.md)** - JVM-specific UI test utilities (Kaspresso-inspired)

#### App Modules
- **[app/android/README.md](../app/android/README.md)** - Android application
- **[app/desktop/README.md](../app/desktop/README.md)** - Desktop JVM application
- **[app/web/README.md](../app/web/README.md)** - Web (Wasm) application
- **[app/ios-kit/README.md](../app/ios-kit/README.md)** - iOS framework (CocoaPods)
- **[app/iosApp/README.md](../app/iosApp/README.md)** - iOS application (Swift)

#### Build Modules
- **[build-logic/README.md](../build-logic/README.md)** - Gradle convention plugins
- **[thirdparty/README.md](../thirdparty/README.md)** - Vendored dependencies

## Quick Start

- **New to project?** → [ARCHITECTURE.md](ARCHITECTURE.md)
- **Want to contribute?** → [CONTRIBUTING.md](../CONTRIBUTING.md)
- **AI agent?** → [AI_AGENT_GUIDE.md](AI_AGENT_GUIDE.md)
- **Writing tests?** → [TESTING_GUIDE.md](TESTING_GUIDE.md)

## Documentation by Topic

- **Architecture & Design** → [ARCHITECTURE.md](ARCHITECTURE.md)
- **Development & Code Style** → [CONTRIBUTING.md](../CONTRIBUTING.md)
- **Testing** → [TESTING_GUIDE.md](TESTING_GUIDE.md)
- **AI Agent Tasks** → [AI_AGENT_GUIDE.md](AI_AGENT_GUIDE.md)
- **Manual Build & Install** → [MANUAL_BUILD_INSTALL.md](MANUAL_BUILD_INSTALL.md)
- **Version Management & CI/CD** → [VERSION_MANAGEMENT_GUIDE.md](VERSION_MANAGEMENT_GUIDE.md)
- **Platform-Specific** → Module READMEs in `app/` directories
- **Data & Persistence** → Module READMEs in `core/data/` directories

## Key Concepts

### Project Structure
```
NoteDelight/
├── core/               # Core business logic and presentation
│   ├── domain/         # Pure business logic (no dependencies)
│   ├── presentation/   # ViewModels, state management
│   ├── data/           # Database implementations
│   └── test/           # Shared test utilities
├── ui/                 # User interface
│   ├── shared/         # 100% shared Compose UI
│   ├── test/           # Multiplatform Compose UI tests
│   └── test-jvm/       # JVM-specific UI test utilities
├── app/                # Platform-specific applications
│   ├── android/        # Android app
│   ├── desktop/        # Desktop app
│   ├── web/            # Web app (Wasm)
│   ├── ios-kit/        # iOS framework
│   └── iosApp/         # iOS app (Swift)
├── build-logic/        # Gradle convention plugins
├── thirdparty/         # Vendored dependencies
└── docs/               # This documentation
```

### Technology Stack

**Languages:**
- Kotlin (multiplatform shared code)
- Swift (iOS app shell)

**UI:**
- Compose Multiplatform (100% shared UI)

**Database:**
- SQLDelight (default, multiplatform)
- Room (alternative, experimental KMP)
- SQLCipher (encryption for Android/iOS/Desktop JVM)

**Architecture:**
- Clean Architecture
- MVVM pattern
- Unidirectional data flow

**DI:**
- Koin (multiplatform dependency injection)

**Testing:**
- Kotlin Test (multiplatform)
- JUnit (Android)
- Mockito (mocking)
- Compose Test (UI)
- Kaspresso-inspired pattern (UI tests)

## Build Commands

```bash
./gradlew build                    # Build everything
./gradlew test                     # Run tests
./gradlew :app:android:installDebug # Android app
./gradlew :app:desktop:run         # Desktop app
./gradlew :app:web:wasmJsBrowserDevelopmentRun --continuous  # Web app
```

## Platform Support

| Platform | Status | Encryption | UI | Testing | Adaptive |
|----------|--------|------------|----|---------|----------|
| Android  | ✅ Production | ✅ SQLCipher | ✅ Compose | ✅ Full | ✅ |
| iOS      | ✅ Production | ✅ SQLCipher | ✅ Compose | ⚠️ Manual | ✅ |
| Desktop  | ✅ Production | ✅ SQLCipher | ✅ Compose | ✅ Full | ✅ |
| Web      | ⚠️ Experimental | ❌ Browser limitation | ✅ Compose | ⚠️ Limited | ✅ |

## Contributing

- **General guidelines** → [CONTRIBUTING.md](../CONTRIBUTING.md)
- **Module-specific** → Check module READMEs
- **Testing** → [TESTING_GUIDE.md](TESTING_GUIDE.md)

## Diagrams

Visual documentation:
- [Architecture Diagram](diagrams/architecture.png) - System architecture overview
- [CI/CD Diagram](diagrams/ci_cd.png) - Continuous integration/deployment workflows

## Additional Resources

- [Main README](../README.md) - Project overview
- [GitHub Repository](https://github.com/softartdev/NoteDelight)

