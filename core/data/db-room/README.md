# Core Data - Room Module

## Overview

The `core:data:db-room` module implements the **data layer** using [Room](https://developer.android.com/training/data-storage/room) - Android's persistence library. This is an **alternative database implementation** to SQLDelight, primarily for comparison and Android-focused development.

## Purpose

- Provide Room-based data layer implementation
- Support database encryption via SQLCipher
- Implement `NoteDAO` and `DatabaseHolder` interfaces from domain layer
- Demonstrate multiplatform Room usage (Android, iOS, Desktop)
- Offer an alternative to SQLDelight for teams preferring Room

## Architecture

```
core:data:db-room (Data Layer - Room implementation)
    ├── src/
    │   ├── commonMain/
    │   │   └── kotlin/
    │   │       └── com/softartdev/notedelight/
    │   │           ├── NoteRoomDAO.kt
    │   │           ├── NoteRoomDb.kt
    │   │           ├── NoteRoomFactory.kt
    │   │           ├── SafeRepoRoom.kt
    │   │           └── converter/
    │   │               └── LocalDateTimeConverter.kt
    │   ├── androidMain/      # Android + Room + SQLCipher
    │   ├── iosMain/          # iOS + Room
    │   ├── jvmMain/          # Desktop + Room
    │   └── commonTest/       # Shared tests
    └── build.gradle.kts
```

## Key Components

### Room Database Definition

```kotlin
@Database(
    entities = [NoteEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class NoteRoomDb : RoomDatabase() {
    abstract fun noteDao(): NoteRoomDAO
}
```

### DAO Implementation

```kotlin
@Dao
interface NoteRoomDAO : NoteDAO {
    @Query("SELECT * FROM Note ORDER BY dateModified DESC")
    override val pagingDataFlow: Flow<PagingData<Note>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(note: Note)
    
    @Delete
    override suspend fun delete(note: Note)
    
    // ... more operations
}
```

### Type Converters

- `LocalDateTimeConverter`: Converts `LocalDateTime` to/from Long (timestamp)
- Required for Room to handle custom types

### Repository Implementation

- `SafeRepoRoom`: Thread-safe repository using Room
- Manages database encryption state
- Implements domain layer interfaces

### Database Factory

- `NoteRoomFactory`: Creates Room databases with platform-specific builders
- Handles encryption setup
- Manages database file paths

## Platform-Specific Implementations

### Android (`androidMain/`)

- **Database**: Room with native Android support
- **Encryption**: ✅ SQLCipher via `SafeRoom` library
- **Storage**: App-specific directory
- **KSP**: Kotlin Symbol Processing for code generation
- **Dependencies**:
  - `androidx.room.runtime`
  - `androidx.room.ktx`
  - `commonsware.saferoom`
  - `android.sqlcipher`

### iOS (`iosMain/`)

- **Database**: Room via KMP support
- **Encryption**: ✅ SQLCipher support
- **Storage**: iOS Documents directory
- **KSP**: Code generation for iOS target
- **Dependencies**:
  - `androidx.room.runtime`
  - SQLCipher pod

### Desktop JVM (`jvmMain/`)

- **Database**: Room with JDBC driver
- **Encryption**: ❌ Not implemented
- **Storage**: User home directory
- **Dependencies**:
  - `androidx.room.runtime`
  - JDBC SQLite driver

## Database Encryption

Room with SQLCipher works similarly to SQLDelight:

```kotlin
// Android with encryption
val factory = SafeRoomSupportFactory.fromUser(Passphrase(password))
val db = Room.databaseBuilder(context, NoteRoomDb::class.java, "notes.db")
    .openHelperFactory(factory)
    .build()
```

## Room KSP Code Generation

Room uses **Kotlin Symbol Processing (KSP)** for compile-time code generation:

```kotlin
// In build.gradle.kts
plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    ksp("androidx.room:room-compiler:$roomVersion")
}
```

Generated code is in `build/generated/ksp/`.

## Pagination Support

Room has built-in Paging3 support:

```kotlin
@Query("SELECT * FROM Note ORDER BY dateModified DESC")
fun pagingSource(): PagingSource<Int, Note>

// In DAO implementation
override val pagingDataFlow: Flow<PagingData<Note>> = Pager(
    config = PagingConfig(pageSize = 20),
    pagingSourceFactory = { noteDao().pagingSource() }
).flow
```

## Database Migrations

Room migrations are defined programmatically:

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Note ADD COLUMN favorite INTEGER DEFAULT 0 NOT NULL")
    }
}

Room.databaseBuilder()
    .addMigrations(MIGRATION_1_2)
    .build()
```

## Multiplatform Support

- ✅ **Android** - Full support with encryption
- ✅ **iOS** - Experimental multiplatform Room support
- ✅ **Desktop JVM** - Full support (no encryption)
- ❌ **Web** - Not supported (Room doesn't support Web)

## Dependencies

### Common
- `core:domain` - Domain interfaces and models
- `androidx.room.runtime` - Room runtime
- `androidx.room.ktx` - Kotlin extensions
- `androidx.paging.common` - Pagination
- `kotlinx-datetime` - Date/time handling
- `kotlinx-coroutines` - Async operations

### Code Generation
- `androidx.room.compiler` (KSP processor)

### Platform-Specific
- Android: SQLCipher + SafeRoom
- iOS: SQLCipher pod
- JVM: JDBC SQLite driver

## Testing

### Unit Tests
Room provides in-memory database for testing:

```kotlin
@RunWith(AndroidJUnit4::class)
class NoteRoomDAOTest {
    private lateinit var database: NoteRoomDb
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            klass = NoteRoomDb::class.java
        ).build()
    }
    
    @After
    fun teardown() {
        database.close()
    }
}
```

### Running Tests

```bash
# All tests
./gradlew :core:data:db-room:test

# Platform-specific
./gradlew :core:data:db-room:testDebugUnitTest
./gradlew :core:data:db-room:iosSimulatorArm64Test
```

## Switching to Room

To use Room instead of SQLDelight, modify `gradle.properties`:

```properties
# Switch to Room
CORE_DATA_DB_MODULE=:core:data:db-room
```

Then rebuild the project:

```bash
./gradlew clean build
```

## Advantages of Room

1. **Android-native**: Deep Android integration
2. **Compile-time verification**: SQL queries validated at compile time
3. **Built-in migrations**: Migration framework included
4. **Paging support**: Native Paging3 integration
5. **Type converters**: Automatic type conversion
6. **Annotation-based**: Familiar Android development patterns

## Limitations

1. **No Web support**: Room doesn't work in browsers
2. **Limited multiplatform**: Primarily Android-focused
3. **Larger binary**: Room adds more code than SQLDelight
4. **KSP requirement**: Requires KSP for code generation

## Comparison: Room vs SQLDelight

| Feature | Room | SQLDelight |
|---------|------|------------|
| SQL-first | ❌ Annotation-based | ✅ Pure SQL |
| Web support | ❌ | ✅ |
| Multiplatform | ⚠️ Experimental | ✅ Native |
| Code gen | KSP | Gradle plugin |
| Paging | Built-in | Custom extension |
| Learning curve | Android-familiar | SQL-familiar |
| Binary size | Larger | Smaller |

## AI Agent Guidelines

**See [CONTRIBUTING.md](../CONTRIBUTING.md) for general guidelines.**

### Room-Specific
- Use `@Entity`, `@Dao`, `@Database` correctly
- Ensure KSP is configured for all platforms
- Register all custom type converters
- Handle iOS/JVM differences
- Use in-memory databases for tests
- Use `suspend` for all async operations

## Best Practices

### Entity Definition

```kotlin
@Entity(tableName = "Note")
data class NoteEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val text: String,
    val dateCreated: LocalDateTime,
    val dateModified: LocalDateTime
)
```

### DAO Queries

```kotlin
@Dao
interface NoteRoomDAO {
    @Query("SELECT * FROM Note WHERE id = :id")
    suspend fun getById(id: Long): Note?
    
    @Query("DELETE FROM Note WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<Note>)
}
```

### Database Builder

```kotlin
val db = Room.databaseBuilder(
    context = context,
    klass = NoteRoomDb::class.java,
    name = "notes.db"
)
    .addMigrations(MIGRATION_1_2)
    .fallbackToDestructiveMigration() // Use with caution!
    .build()
```

## Related Modules

- **Used by**: `ui:shared`, `app:android` (when selected)
- **Depends on**: `core:domain`
- **Alternative**: `core:data:db-sqldelight` (default)

## Resources

- [Room Documentation](https://developer.android.com/training/data-storage/room)
- [Room KMP](https://developer.android.com/kotlin/multiplatform/room)
- [KSP](https://kotlinlang.org/docs/ksp-overview.html)
- [SQLCipher](https://www.zetetic.net/sqlcipher/)

