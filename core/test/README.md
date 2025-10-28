# Core Test Module

## Overview

The `core:test` module provides **shared testing utilities and helpers** for writing tests across all platforms. It contains common test fixtures, mock implementations, and utility functions used by other modules' test suites.

## Purpose

- Provide reusable test utilities and helpers
- Share test data fixtures across modules
- Offer mock implementations of domain interfaces
- Simplify test setup and teardown
- Ensure consistent testing patterns across the project

## Architecture

```
core:test (Shared Testing Utilities)
    ├── src/
    │   ├── commonMain/
    │   │   └── kotlin/
    │   │       └── com/softartdev/notedelight/
    │   │           ├── TestData.kt           # Test fixtures
    │   │           ├── MockNoteDAO.kt        # Mock implementations
    │   │           └── TestCoroutineRule.kt  # Test utilities
    │   ├── androidMain/
    │   │   └── kotlin/
    │   │       └── # Android-specific test utilities (Mockito, etc.)
    │   ├── iosMain/
    │   ├── jvmMain/
    │   └── wasmJsMain/
    └── build.gradle.kts
```

## Key Components

### Test Fixtures

Predefined test data for consistent testing:

```kotlin
object TestData {
    val sampleNote = Note(
        id = 1L,
        title = "Test Note",
        text = "Test content",
        dateCreated = LocalDateTime(2024, 1, 1, 12, 0),
        dateModified = LocalDateTime(2024, 1, 1, 12, 0)
    )
    
    val sampleNotes = listOf(
        sampleNote,
        // ... more test notes
    )
}
```

### Mock Implementations

Mock versions of domain interfaces for testing:

```kotlin
class MockNoteDAO : NoteDAO {
    private val notes = mutableListOf<Note>()
    
    override val listFlow: Flow<List<Note>> = flow { emit(notes) }
    
    override suspend fun insert(note: Note) {
        notes.add(note)
    }
    
    // ... other mock implementations
}
```

### Coroutine Test Utilities

Helpers for testing coroutines:

```kotlin
class TestCoroutineRule : TestWatcher() {
    val testDispatcher = StandardTestDispatcher()
    
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }
    
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
```

## Multiplatform Support

This module supports all platforms:

- ✅ **Android** (androidTarget) - With Mockito support
- ✅ **iOS** (iosArm64, iosSimulatorArm64)
- ✅ **Desktop JVM** (jvm)
- ✅ **Web** (wasmJs)

## Dependencies

### Core Dependencies
- `core:domain` - Domain models and interfaces
- `kotlinx-coroutines-test` - Coroutine testing utilities
- `napier` - Logging

### Android-Specific
- `mockito-core` - Mocking framework
- `mockito-kotlin` - Kotlin DSL for Mockito
- `mockito-inline` - Inline mocking support

### Common Test Framework
- `kotlin-test` - Multiplatform test framework

## Usage in Other Modules

### In Module Tests

Add as test dependency:

```kotlin
// In build.gradle.kts
sourceSets {
    commonTest.dependencies {
        implementation(project(":core:test"))
    }
}
```

### Example Usage

```kotlin
// In a ViewModel test
class MainViewModelTest {
    private lateinit var viewModel: MainViewModel
    private val mockDAO = MockNoteDAO()
    
    @Test
    fun `should load notes on init`() = runTest {
        // Arrange
        mockDAO.insertAll(TestData.sampleNotes)
        viewModel = MainViewModel(mockDAO, ...)
        
        // Act
        viewModel.loadNotes()
        
        // Assert
        assertEquals(TestData.sampleNotes.size, viewModel.state.value.notes.size)
    }
}
```

## Testing Patterns

### Coroutine Testing

Use `runTest` for coroutine tests:

```kotlin
@Test
fun `test async operation`() = runTest {
    // Test code with suspending functions
    val result = someUseCase()
    assertEquals(expected, result)
}
```

### Mock DAO Testing

```kotlin
@Test
fun `test DAO operations`() = runTest {
    val mockDAO = MockNoteDAO()
    
    // Insert test data
    mockDAO.insert(TestData.sampleNote)
    
    // Verify
    val notes = mockDAO.listFlow.first()
    assertTrue(notes.contains(TestData.sampleNote))
}
```

### Android Mockito Testing

```kotlin
// Android-specific test
@Test
fun `test with mockito`() {
    val mockRepo = mock<SafeRepo>()
    whenever(mockRepo.noteDAO).thenReturn(mockDAO)
    
    // Test with mock
    val viewModel = MainViewModel(mockRepo, ...)
    // ... assertions
}
```

## Test Utilities

### Date/Time Helpers

```kotlin
fun createTestDate(year: Int, month: Int, day: Int) = 
    LocalDateTime(year, month, day, 0, 0)

fun now() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
```

### Flow Testing

```kotlin
@Test
fun `test flow emissions`() = runTest {
    val flow = mockDAO.listFlow
    
    // Test first emission
    val firstEmission = flow.first()
    assertEquals(emptyList(), firstEmission)
    
    // Modify data
    mockDAO.insert(TestData.sampleNote)
    
    // Test second emission
    val secondEmission = flow.drop(1).first()
    assertEquals(1, secondEmission.size)
}
```

## Test Data Builder Pattern

Create complex test data easily:

```kotlin
fun buildNote(
    id: Long = 1L,
    title: String = "Test",
    text: String = "Content",
    dateCreated: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    dateModified: LocalDateTime = dateCreated
) = Note(id, title, text, dateCreated, dateModified)

// Usage
val note = buildNote(id = 5, title = "Custom Title")
```

## AI Agent Guidelines

**See [CONTRIBUTING.md](../CONTRIBUTING.md) for general guidelines.**

### Test-Specific
- Add helpers that benefit multiple modules
- Keep utilities in `commonMain` when possible
- Provide realistic but simple test data
- Keep mocks simple and predictable
- This module is test-only
- Minimize external dependencies

## Best Practices

### Creating Test Fixtures

```kotlin
object NoteFixtures {
    fun createNote(
        id: Long = 1L,
        title: String = "Test Note $id"
    ) = Note(
        id = id,
        title = title,
        text = "Test content",
        dateCreated = createTestDate(),
        dateModified = createTestDate()
    )
    
    fun createNotes(count: Int) = (1..count).map { createNote(it.toLong()) }
}
```

### Mock DAO Pattern

```kotlin
class TestNoteDAO : NoteDAO {
    private val notes = MutableStateFlow<List<Note>>(emptyList())
    
    override val listFlow: Flow<List<Note>> = notes.asStateFlow()
    
    override suspend fun insert(note: Note) {
        notes.value = notes.value + note
    }
    
    suspend fun clear() {
        notes.value = emptyList()
    }
}
```

### Coroutine Test Setup

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class TestCoroutineSetup {
    val testDispatcher = StandardTestDispatcher()
    val testScope = TestScope(testDispatcher)
    
    fun runTest(block: suspend TestScope.() -> Unit) = 
        testScope.runTest(block)
}
```

## Testing Anti-Patterns to Avoid

1. ❌ **Don't** use real databases in unit tests - use mocks
2. ❌ **Don't** hardcode dates - use test date builders
3. ❌ **Don't** use `delay()` - use `advanceTimeBy()` in tests
4. ❌ **Don't** test implementation details - test behavior
5. ❌ **Don't** create complex test hierarchies - keep tests flat

## Related Modules

- **Used by**: All modules with test code (`core:presentation`, `core:data`, `ui:shared`, etc.)
- **Depends on**: `core:domain`
- **Scope**: Test-only (not included in production builds)

## Running Tests

This module itself has minimal tests, but enables testing in other modules:

```bash
# Test the test utilities (meta-testing)
./gradlew :core:test:test
```

## Contributing Test Utilities

When adding new test utilities:

1. **Check for duplicates**: Search existing utilities first
2. **Add documentation**: Document purpose and usage
3. **Write examples**: Include usage examples in docs
4. **Platform-agnostic first**: Try to keep in `commonMain`
5. **Test the utilities**: Even test utilities need tests!

## Resources

- [Kotlin Test Documentation](https://kotlinlang.org/api/latest/kotlin.test/)
- [Coroutine Testing](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/)
- [Mockito](https://site.mockito.org/)
- [Turbine (Flow Testing)](https://github.com/cashapp/turbine)

