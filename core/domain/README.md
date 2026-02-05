# Core Domain Module

## Overview

The `core:domain` module is the heart of the application's business logic layer. It contains platform-independent business entities, use cases, and repository interfaces following **Clean Architecture** principles. This module has **no dependencies on UI, framework, or data implementation details**.

## Purpose

- Define core business models and entities
- Implement use cases (business logic operations)
- Define repository interfaces (contracts for data layer)
- Provide platform-agnostic utility functions

## Architecture

This module follows **Clean Architecture** and **Domain-Driven Design** principles:

```
core:domain (Business Logic - Pure Kotlin)
    ├── model/          # Business entities
    ├── db/             # Repository interfaces
    ├── usecase/        # Business logic operations
    │   ├── note/       # Note operations (CRUD)
    │   └── crypt/      # Encryption operations
    ├── repository/     # Repository implementations
    └── util/           # Platform utilities
```

## Key Components

### Models (`model/`)

Domain entities representing core business concepts:

- `Note`: Immutable data class representing a note with id, title, text, and timestamps
- `PlatformSQLiteState`: Represents database encryption state
- `PlatformSQLiteThrowable`: Domain-specific exception wrapper

### Use Cases (`usecase/`)

Single-responsibility business operations following the **Command Pattern**:

#### Note Operations (`usecase/note/`)
- `CreateNoteUseCase`: Creates a new note with generated ID
- `SaveNoteUseCase`: Updates existing note content
- `UpdateTitleUseCase`: Updates note title
- `DeleteNoteUseCase`: Removes a note

#### Cryptography Operations (`usecase/crypt/`)
- `CheckPasswordUseCase`: Validates database password
- `ChangePasswordUseCase`: Changes database encryption password
- `CheckSqlCipherVersionUseCase`: Verifies SQLCipher availability

#### Backup Operations
Database import/export lives in the `feature:backup:domain` module, which owns the backup use cases and
the platform-specific file transfer implementation.

### Repository Interfaces (`db/`)

Contracts for data access (implemented by data layer):

- `NoteDAO`: Note data access operations
- `DatabaseHolder`: Database lifecycle management

### Repository Implementations (`repository/`)

- `SafeRepo`: Thread-safe repository wrapper managing database operations

### Platform Utilities (`util/`)

Platform-specific implementations using `expect`/`actual` pattern:

- `CoroutineDispatchers`: Platform-specific coroutine dispatchers
- `platformName`: Platform detection (Android, iOS, JVM, Web)
- `platformDate`: Platform-specific date/time operations
- `runBlockingAll`: Platform-specific blocking coroutine execution

## Multiplatform Support

This module is **Kotlin Multiplatform** and supports:

- ✅ **Android** (androidTarget)
- ✅ **iOS** (iosArm64, iosSimulatorArm64)
- ✅ **Desktop JVM** (jvm)
- ✅ **Web** (wasmJs)

Platform-specific code is organized using `expect`/`actual` declarations in:
- `commonMain/` - Shared code
- `androidMain/` - Android-specific implementations
- `iosMain/` - iOS-specific implementations
- `jvmMain/` - Desktop JVM implementations
- `wasmJsMain/` - Web implementations

## Dependencies

### Core Dependencies
- `kotlinx-coroutines` - Asynchronous programming
- `kotlinx-datetime` - Multiplatform date/time handling
- `androidx-paging-common` - Pagination support
- `napier` - Multiplatform logging

### Testing
- `kotlin-test` - Unit testing framework

## Design Patterns

1. **Clean Architecture**: Domain layer isolated from external dependencies
2. **Use Case Pattern**: Each business operation is a separate use case
3. **Repository Pattern**: Abstract data access behind interfaces
4. **Dependency Inversion**: Domain defines interfaces, data layer implements
5. **Immutability**: Domain models are immutable data classes
6. **Operator Overloading**: Use cases implement `operator fun invoke()` for clean syntax

## Usage Example

```kotlin
// Use case invocation (from ViewModel)
val createNoteUseCase = CreateNoteUseCase(noteDAO)
val noteId = createNoteUseCase(title = "My Note", text = "Content")

// Use cases are callable as functions due to invoke() operator
val saveNoteUseCase = SaveNoteUseCase(noteDAO)
saveNoteUseCase(noteId, "Updated content")
```

## Testing

Unit tests are located in `commonTest/` and can run on all platforms:

```bash
# Run all tests
./gradlew :core:domain:test

# Platform-specific tests
./gradlew :core:domain:jvmTest
./gradlew :core:domain:iosSimulatorArm64Test
```

## AI Agent Guidelines

**See [CONTRIBUTING.md](../CONTRIBUTING.md) for general guidelines.**

### Domain-Specific
- Keep it pure: No framework or UI dependencies
- Use cases should be simple: Single responsibility, stateless
- Models are immutable: Use `data class` with `val` properties
- Use `expect`/`actual` only when necessary
- Write platform-agnostic tests in `commonTest/`

## Related Modules

- **Used by**: `core:presentation`, `core:data`, `ui:shared`
- **Depends on**: None (pure Kotlin)
