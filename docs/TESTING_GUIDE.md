# Testing Guide

This document describes the testing strategy, patterns, and best practices for NoteDelight.

## Testing Philosophy

We follow the **Testing Pyramid** approach:

```
         ┌───────────┐
         │    E2E    │  ← Few, slow, high value
         │  (UI)     │
         └───────────┘
       ┌─────────────┐
       │ Integration │   ← Some, medium speed
       │  (Data)     │
       └─────────────┘
     ┌─────────────────┐
     │  Unit Tests     │  ← Many, fast, low cost
     │ (Domain/VM)     │
     └─────────────────┘
```

## Test Categories

### Unit Tests (Most)

**Target**: Domain layer (use cases) and Presentation layer (ViewModels)

**Characteristics**:
- Fast execution (< 1 second)
- No dependencies on external systems
- Use mocks/fakes for dependencies
- Test single units of code

**Location**:
- `core/domain/src/commonTest/`
- `core/presentation/src/androidUnitTest/`
- `core/data/db-sqldelight/src/androidUnitTest`
- `core/data/db-sqldelight/src/commonTest`
- `core/data/db-sqldelight/src/wasmJsTest`

**Framework**: 
- Kotlin Test (multiplatform)
- JUnit (Android)
- Mockito (mocking)
- Turbine (Flow testing)

**Example**:
```kotlin
class CreateNoteUseCaseTest {
    
    @Test
    fun `should create note with generated ID`() = runTest {
        // Given
        val mockDAO = MockNoteDAO()
        val useCase = CreateNoteUseCase(mockDAO)
        
        // When
        val noteId = useCase("Test Title", "Test Text")
        
        // Then
        assertEquals(1L, noteId)
        assertEquals(1, mockDAO.notes.size)
    }
    
    @Test
    fun `should throw exception for blank title`() = runTest {
        val useCase = CreateNoteUseCase(MockNoteDAO())
        
        assertFailsWith<IllegalArgumentException> {
            useCase("", "Text")
        }
    }
}
```

### Integration Tests (Some)

**Target**: Data layer (DAOs, Repositories)

**Characteristics**:
- Medium execution time (1-5 seconds)
- Use real database (in-memory)
- Test multiple components together
- Verify data persistence

**Location**:
- `core/domain/src/commonTest`
- `core/data/db-sqldelight/src/iosTest/kotlin`
- `core/presentation/src/androidUnitTest`

**Framework**:
- In-memory SQLite
- SQLDelight/Room test utilities

**Example**:
```kotlin
class NoteSQLDelightDAOTest {
    private lateinit var database: NoteDb
    private lateinit var dao: NoteSQLDelightDAO
    
    @Before
    fun setup() {
        // In-memory database
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        NoteDb.Schema.create(driver)
        database = NoteDb(driver)
        dao = NoteSQLDelightDAO(database)
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    @Test
    fun `should insert and retrieve note`() = runTest {
        // Given
        val note = TestData.sampleNote
        
        // When
        dao.insert(note)
        val retrieved = dao.getById(note.id)
        
        // Then
        assertEquals(note, retrieved)
    }
}
```

### UI Tests (Few)

**Target**: Complete user flows

**Characteristics**:
- Slow execution (10-60 seconds)
- Test full application stack
- User-facing scenarios
- Critical paths only

**Location**:
- `app/android/src/androidTest/` (Android)
- `app/desktop/src/jvmTest/` (Desktop)
- `ui/test-jvm/src/main/kotlin/` (Shared test framework)

**Framework**:
- Compose Test
- Espresso (Android)
- Kaspresso-inspired pattern (custom)

**Example**:
```kotlin
class CrudTestCase(
    composeTestRule: ComposeContentTestRule
) : () -> Unit, BaseTestCase(composeTestRule) {
    
    override fun invoke() = runTest {
        // Navigate and create note
        mainTestScreen {
            fabSNI.performClick()
            
            noteScreen {
                textFieldSNI.performTextInput("Test Note")
                saveButtonSNI.performClick()
                backButtonSNI.performClick()
            }
            
            // Verify note appears
            noteItemTitleText = "Test Note"
            composeTestRule.waitUntilDisplayed(blockSNI = ::noteListItemSNI)
            noteListItemSNI.assertIsDisplayed()
            
            // Delete note
            noteListItemSNI.performClick()
            noteScreen {
                deleteNoteMenuButtonSNI.performClick()
                commonDialog {
                    yesDialogButtonSNI.performClick()
                }
            }
            
            // Verify empty state
            composeTestRule.waitUntilDisplayed(blockSNI = ::emptyResultLabelSNI)
        }
    }
}
```

## Testing by Layer

### Domain Layer Testing

**What to test**:
- ✅ Use case business logic
- ✅ Data validation
- ✅ Error handling
- ✅ Edge cases

**What NOT to test**:
- ❌ Simple data classes (no logic)
- ❌ Interfaces (no implementation)

**Example structure**:
```kotlin
class FeatureUseCaseTest {
    private lateinit var useCase: FeatureUseCase
    private lateinit var mockDAO: NoteDAO
    
    @Before
    fun setup() {
        mockDAO = mock()
        useCase = FeatureUseCase(mockDAO)
    }
    
    @Test
    fun `happy path test`() { }
    
    @Test
    fun `error case test`() { }
    
    @Test
    fun `edge case test`() { }
}
```

### Presentation Layer Testing

**What to test**:
- ✅ State transitions
- ✅ User action handling
- ✅ Navigation triggers
- ✅ Error handling
- ✅ Loading states

**Setup required**:
- `InstantTaskExecutorRule` - For LiveData/StateFlow
- `MainDispatcherRule` - For coroutines

**Example**:
```kotlin
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    private lateinit var viewModel: MainViewModel
    private lateinit var mockRepo: SafeRepo
    private lateinit var mockRouter: Router
    
    @Before
    fun setup() {
        mockRepo = mock()
        mockRouter = mock()
        viewModel = MainViewModel(mockRepo, mockRouter, TestDispatchers)
    }
    
    @Test
    fun `loading notes should update state from Loading to Success`() = runTest {
        // Given
        val notes = flowOf(PagingData.from(TestData.sampleNotes))
        whenever(mockRepo.noteDAO.pagingDataFlow).thenReturn(notes)
        
        // When
        viewModel.loadNotes()
        advanceUntilIdle()
        
        // Then
        val state = viewModel.stateFlow.value
        assertTrue(state is NoteListResult.Success)
    }
    
    @Test
    fun `clicking note should navigate to details`() {
        // When
        viewModel.onNoteClicked(123L)
        
        // Then
        verify(mockRouter).navigate(AppNavGraph.Details(123L))
    }
}
```

### Data Layer Testing

**What to test**:
- ✅ CRUD operations
- ✅ Query correctness
- ✅ Data mapping
- ✅ Transaction behavior

**Use in-memory database**:
```kotlin
@Before
fun setup() {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    NoteDb.Schema.create(driver)
    database = NoteDb(driver)
}
```

### UI Layer Testing

**What to test**:
- ✅ Critical user flows (CRUD)
- ✅ Navigation paths
- ✅ Form validation
- ✅ Error messages

**Screen Object Pattern** (Kaspresso-inspired):
```kotlin
// Screen object
class MainTestScreen(private val composeTestRule: ComposeContentTestRule) {
    val fabSNI: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithContentDescription("Create Note")
    
    fun screen(block: MainTestScreen.() -> Unit) = apply(block)
}

// Test case
class FeatureTestCase(
    composeTestRule: ComposeContentTestRule
) : BaseTestCase(composeTestRule) {
    
    override fun invoke() = runTest {
        mainTestScreen {
            // Test steps
        }
    }
}
```

## Testing Patterns

### AAA Pattern (Arrange-Act-Assert)

```kotlin
@Test
fun testSomething() {
    // Arrange (Given)
    val input = setupTestData()
    val expected = expectedResult()
    
    // Act (When)
    val actual = performOperation(input)
    
    // Assert (Then)
    assertEquals(expected, actual)
}
```

### Given-When-Then Naming

```kotlin
@Test
fun `given valid note when saving then returns success`() { }

@Test
fun `given network error when loading then shows error state`() { }

@Test
fun `given empty database when querying then returns empty list`() { }
```

### Test Fixtures

Create reusable test data:

```kotlin
object TestData {
    val sampleNote = Note(
        id = 1L,
        title = "Test Note",
        text = "Test content",
        dateCreated = LocalDateTime(2024, 1, 1, 12, 0),
        dateModified = LocalDateTime(2024, 1, 1, 12, 0)
    )
    
    fun createNote(
        id: Long = 1L,
        title: String = "Test Note $id"
    ) = Note(id, title, "Content", testDate(), testDate())
    
    fun createNotes(count: Int) = 
        (1..count).map { createNote(it.toLong()) }
}
```

### Mock vs Fake

**Mock**: Verify interactions
```kotlin
val mockDAO = mock<NoteDAO>()
whenever(mockDAO.insert(any())).thenReturn(Unit)

// Verify
verify(mockDAO).insert(note)
verify(mockDAO, times(1)).insert(any())
```

**Fake**: Provide simple implementation
```kotlin
class FakeNoteDAO : NoteDAO {
    val notes = mutableListOf<Note>()
    
    override suspend fun insert(note: Note) {
        notes.add(note)
    }
    
    override suspend fun getById(id: Long) = 
        notes.find { it.id == id }
}
```

## Testing Tools

### Kotlin Test

Multiplatform testing:
```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Test
fun myTest() {
    assertEquals(expected, actual)
}
```

### Coroutines Test

Testing suspend functions:
```kotlin
@Test
fun testCoroutine() = runTest {
    val result = suspendingFunction()
    assertEquals(expected, result)
}
```

Advanced:
```kotlin
@Test
fun testWithDelay() = runTest {
    viewModel.action()
    advanceTimeBy(1000) // Fast-forward virtual time
    advanceUntilIdle()  // Run all pending coroutines
    
    assertEquals(expected, viewModel.state.value)
}
```

### Turbine (Flow Testing)

Testing Flows:
```kotlin
@Test
fun testFlow() = runTest {
    repository.notesFlow.test {
        // Assert initial emission
        assertEquals(emptyList(), awaitItem())
        
        // Trigger change
        repository.addNote(note)
        
        // Assert updated emission
        assertEquals(listOf(note), awaitItem())
        
        // Verify no more emissions
        cancelAndIgnoreRemainingEvents()
    }
}
```

### Mockito

Creating mocks:
```kotlin
val mock = mock<Interface>()
val mockWithAnswer = mock<Interface> {
    on { method() } doReturn value
}

// Stubbing
whenever(mock.method()).thenReturn(value)
whenever(mock.method(any())).thenAnswer { invocation ->
    invocation.getArgument(0)
}

// Verification
verify(mock).method()
verify(mock, times(2)).method()
verify(mock, never()).method()
verifyNoMoreInteractions(mock)
```

### Compose Test

Finding nodes:
```kotlin
composeTestRule.onNodeWithText("Button")
composeTestRule.onNodeWithContentDescription("Icon")
composeTestRule.onNodeWithTag("unique_tag")
composeTestRule.onNode(hasClickAction())
```

Actions:
```kotlin
node.performClick()
node.performTextInput("text")
node.performScrollTo()
node.performGesture { swipeLeft() }
```

Assertions:
```kotlin
node.assertIsDisplayed()
node.assertIsNotDisplayed()
node.assertExists()
node.assertDoesNotExist()
node.assertIsEnabled()
node.assertTextEquals("text")
node.assertTextContains("partial")
```

## Running Tests

### Command Line

```bash
./gradlew test                    # All tests
./gradlew :core:domain:test       # Specific module
./gradlew :app:android:connectedCheck  # Android UI tests
./gradlew :app:desktop:jvmTest    # Desktop tests
```

### IDE
Right-click test class → Run, or click green arrow next to test method.

## Test Coverage

### What to aim for

- **Domain layer**: 80-90% (high value)
- **Presentation layer**: 70-80%
- **Data layer**: 60-70% (integration tests)
- **UI layer**: Critical paths only

### Measuring coverage

```bash
# Generate coverage report
./gradlew test jacocoTestReport

# View report
open build/reports/jacoco/test/html/index.html
```

## Best Practices

**See [CONTRIBUTING.md](../CONTRIBUTING.md) for general coding best practices.**

### Testing Specific
- Write tests first (TDD) when possible
- Use descriptive test names (`given_when_then`)
- Test behavior, not implementation
- Keep tests independent
- Mock external dependencies
- Test edge cases and errors

## Common Testing Scenarios

### Testing StateFlow

```kotlin
@Test
fun testStateFlow() = runTest {
    // Collect initial state
    val initialState = viewModel.stateFlow.value
    assertEquals(State.Initial, initialState)
    
    // Trigger action
    viewModel.performAction()
    advanceUntilIdle()
    
    // Collect updated state
    val updatedState = viewModel.stateFlow.value
    assertTrue(updatedState is State.Success)
}
```

### Testing Navigation

```kotlin
@Test
fun testNavigation() {
    val mockRouter = mock<Router>()
    val viewModel = ViewModel(router = mockRouter)
    
    viewModel.navigateToScreen()
    
    verify(mockRouter).navigate(AppNavGraph.TargetScreen)
}
```

### Testing Error Handling

```kotlin
@Test
fun `should handle error gracefully`() = runTest {
    // Given
    whenever(mockUseCase()).thenThrow(RuntimeException("Error"))
    
    // When
    viewModel.performAction()
    advanceUntilIdle()
    
    // Then
    val state = viewModel.stateFlow.value
    assertTrue(state is State.Error)
    assertNotNull(state.message)
}
```

### Testing with Delays

```kotlin
@Test
fun testWithTimeout() = runTest {
    viewModel.startTimer()
    
    // Fast-forward virtual time
    advanceTimeBy(5000)
    
    assertTrue(viewModel.isTimedOut.value)
}
```

## Troubleshooting Tests

### Tests timing out

- Use `runTest` for coroutines
- Call `advanceUntilIdle()`
- Check for infinite loops

### Tests flaking

- Avoid `delay()` in tests
- Use deterministic test data
- Mock time-dependent code
- Ensure test isolation

### Mocks not working

- Verify mock setup: `whenever(...).thenReturn(...)`
- Check method signatures match
- Use `any()` matchers correctly
- Enable mockito-inline for final classes

### UI tests failing

- Add wait utilities
- Use semantic properties
- Check for animations
- Ensure proper test cleanup

## Resources

- [Kotlin Test](https://kotlinlang.org/api/latest/kotlin.test/)
- [Coroutine Testing](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/)
- [Mockito](https://site.mockito.org/)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [Turbine](https://github.com/cashapp/turbine)
- [Testing Best Practices](https://developer.android.com/training/testing/fundamentals)

---

Remember: **Good tests are your safety net for refactoring and a form of documentation.**

