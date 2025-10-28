# Core Data - SQLDelight Module

## Overview

The `core:data:db-sqldelight` module implements the **data layer** using [SQLDelight](https://github.com/cashapp/sqldelight) - a multiplatform SQL database library that generates type-safe Kotlin APIs from SQL statements. This is the **default database implementation** for NoteDelight.

## Purpose

- Implement data access layer using SQLDelight
- Provide multiplatform SQLite database access
- Support database encryption via SQLCipher (Android, iOS)
- Implement `NoteDAO` and `DatabaseHolder` interfaces from domain layer
- Manage database migrations and schema evolution

## Architecture

```
core:data:db-sqldelight (Data Layer - SQLDelight implementation)
    ├── src/
    │   ├── commonMain/
    │   │   ├── kotlin/
    │   │   │   └── com/softartdev/notedelight/
    │   │   │       ├── NoteDatabaseSQLDelightFactory.kt
    │   │   │       ├── NoteDbHelper.kt
    │   │   │       ├── NoteSQLDelightDAO.kt
    │   │   │       ├── SafeRepoSQLDelight.kt
    │   │   │       └── mapper/
    │   │   │           └── LocalDateTimeMapper.kt
    │   │   └── sqldelight/
    │   │       └── com/softartdev/notedelight/db/
    │   │           └── NoteDb.sq (SQL schema)
    │   ├── androidMain/      # Android + SQLCipher
    │   ├── iosMain/          # iOS + SQLCipher
    │   ├── jvmMain/          # Desktop (unencrypted)
    │   └── wasmJsMain/       # Web (sql.js)
    └── build.gradle.kts
```

## Key Components

### SQL Schema (`sqldelight/NoteDb.sq`)

SQLDelight uses `.sq` files for SQL definitions:

```sql
CREATE TABLE IF NOT EXISTS Note (
    id INTEGER PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    text TEXT NOT NULL,
    dateCreated TEXT NOT NULL,
    dateModified TEXT NOT NULL
);

-- Queries
selectAll:
SELECT * FROM Note ORDER BY dateModified DESC;

insert:
INSERT INTO Note(id, title, text, dateCreated, dateModified)
VALUES (?, ?, ?, ?, ?);
```

SQLDelight **generates type-safe Kotlin code** from these SQL statements at compile time.

### DAO Implementation

- `NoteSQLDelightDAO`: Implements `NoteDAO` interface using generated SQLDelight APIs
- Provides CRUD operations and pagination support
- Uses Flow for reactive data streams

### Database Factory

- `NoteDatabaseSQLDelightFactory`: Creates platform-specific database drivers
- Handles encryption setup (SQLCipher) for Android and iOS
- Manages database file paths

### Repository Implementation

- `SafeRepoSQLDelight`: Thread-safe repository implementation
- Manages database lifecycle and encryption state
- Implements `SafeRepo` interface from domain layer

### Date/Time Mapping

- `LocalDateTimeMapper`: Converts between SQLite TEXT and Kotlin `LocalDateTime`
- Ensures consistent date handling across platforms

## Platform-Specific Implementations

### Android (`androidMain/`)

- **Driver**: `AndroidSqliteDriver`
- **Encryption**: ✅ SQLCipher support via `SafeRoom` library
- **Storage**: App-specific directory (`/data/data/.../databases/`)
- **Dependencies**:
  - `sqlDelight.android`
  - `commonsware.saferoom` (SQLCipher wrapper)
  - `android.sqlcipher`

### iOS (`iosMain/`)

- **Driver**: `NativeSqliteDriver`
- **Encryption**: ✅ SQLCipher via CocoaPods
- **Storage**: iOS Documents directory
- **Dependencies**:
  - `sqlDelight.native`
  - SQLCipher pod (specified in `cocoapods {}` block)
  - `stately` (for iOS memory model)

### Desktop JVM (`jvmMain/`)

- **Driver**: `JdbcSqliteDriver` (SQLite JDBC)
- **Encryption**: ❌ Not implemented yet
- **Storage**: User home directory via `appdirs` library
- **Dependencies**:
  - `sqlDelight.jvm`
  - `appdirs` (for platform-specific app directories)

### Web (`wasmJsMain/`)

- **Driver**: Web Worker with `sql.js`
- **Encryption**: ❌ Not supported (browser environment)
- **Storage**: IndexedDB via browser APIs
- **Dependencies**:
  - `sqlDelight.web`
  - `sql.js` NPM package
  - Web Worker setup via webpack

## Database Encryption

### SQLCipher Integration

Encryption is supported on **Android** and **iOS** using SQLCipher:

```kotlin
// Android
val driver = AndroidSqliteDriver(
    schema = NoteDb.Schema,
    context = context,
    name = "notes.db",
    factory = SafeRoomSupportFactory.fromUser(Passphrase(password))
)

// iOS
val driver = NativeSqliteDriver(
    schema = NoteDb.Schema,
    name = "notes.db",
    key = password // SQLCipher key
)
```

### Encryption States

- `PlatformSQLiteState.DOES_NOT_EXIST` - No database file
- `PlatformSQLiteState.UNENCRYPTED` - Database without encryption
- `PlatformSQLiteState.ENCRYPTED` - Database with encryption
- `PlatformSQLiteState.UNDEFINED` - Unknown state

## Pagination Support

Uses custom SQLDelight Paging3 extension from `thirdparty:app:cash:sqldelight:paging3`:

```kotlin
val pagingDataFlow: Flow<PagingData<Note>> = noteDAO.pagingDataFlow
```

This provides seamless integration with Jetpack/Compose Paging library.

## Database Migrations

SQLDelight handles migrations through schema versioning:

```kotlin
NoteDb.Schema.migrate(
    driver = driver,
    oldVersion = 1,
    newVersion = 2
)
```

Migration SQL is defined in `.sqm` files.

## Multiplatform Support

- ✅ **Android** - Full support with encryption
- ✅ **iOS** - Full support with encryption
- ✅ **Desktop JVM** - Full support (no encryption)
- ✅ **Web** - Full support (no encryption, sql.js)

## Dependencies

### Common
- `core:domain` - Domain interfaces and models
- `sqlDelight.runtime` - SQLDelight runtime
- `sqlDelight.coroutinesExt` - Coroutine extensions
- `androidx-paging-common` - Pagination
- `kotlinx-datetime` - Date/time handling
- `kotlinx-coroutines` - Async operations
- `stately-common` - Thread-safe state management

### Platform-Specific

See "Platform-Specific Implementations" section above for details.

## Testing

### Unit Tests
- Mock database testing with in-memory SQLite
- DAO operation tests
- Repository tests

### Integration Tests
- Android instrumented tests (`androidInstrumentedTest/`)
- Platform-specific tests for each target

### Running Tests

```bash
# All tests
./gradlew :core:data:db-sqldelight:test

# Platform-specific
./gradlew :core:data:db-sqldelight:jvmTest
./gradlew :core:data:db-sqldelight:iosSimulatorArm64Test

# Android instrumented tests (requires emulator)
./gradlew :core:data:db-sqldelight:connectedCheck
```

## Switching Database Implementation

To switch from SQLDelight to Room, modify `gradle.properties`:

```properties
# Default: SQLDelight
CORE_DATA_DB_MODULE=:core:data:db-sqldelight

# Alternative: Room
CORE_DATA_DB_MODULE=:core:data:db-room
```

## Code Generation

SQLDelight generates code at compile time:

```bash
# Generate database code
./gradlew :core:data:db-sqldelight:generateCommonMainNoteDbInterface
```

Generated files are in `build/generated/sqldelight/`.

## AI Agent Guidelines

**See [CONTRIBUTING.md](../CONTRIBUTING.md) for general guidelines.**

### SQLDelight-Specific
- Write SQL in `.sq` files, not Kotlin
- Leverage SQLDelight's generated code
- Use `expect`/`actual` for platform-specific drivers
- SQLCipher only on Android/iOS
- Test with in-memory databases
- Use Flow for reactive queries

## Best Practices

### Defining Queries

```sql
-- Named query in NoteDb.sq
selectById:
SELECT * FROM Note WHERE id = ?;

deleteById:
DELETE FROM Note WHERE id = ?;
```

### Using Generated Code

```kotlin
// SQLDelight generates type-safe APIs
val note: Note? = database.noteQueries.selectById(id = 1).executeAsOneOrNull()

database.noteQueries.deleteById(id = 1)
```

### Reactive Queries

```kotlin
// Observe query results as Flow
val notesFlow: Flow<List<Note>> = database.noteQueries
    .selectAll()
    .asFlow()
    .mapToList(Dispatchers.IO)
```

## Related Modules

- **Used by**: `ui:shared`, `app:android`, `app:desktop`, `app:web`, `app:ios-kit`
- **Depends on**: `core:domain`, `thirdparty:app:cash:sqldelight:paging3`
- **Alternative**: `core:data:db-room` (Room implementation)

## Resources

- [SQLDelight Documentation](https://cashapp.github.io/sqldelight/)
- [SQLCipher](https://www.zetetic.net/sqlcipher/)
- [SafeRoom](https://github.com/commonsguy/cwac-saferoom)

