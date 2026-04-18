# feature:console

SQL Console feature modules for executing raw SQL commands against the app database.

## Modules

### feature:console:domain
- `ConsoleTranscriptEntryKind` — `COMMAND`, `OUTPUT`, `STATUS`, `ERROR`
- `ConsoleTranscriptEntry` — domain model for transcript rows
- `ConsoleCommandExecutor` — interface for the data layer to implement
- `ExecuteConsoleCommandUseCase` — orchestrates input validation, semicolon normalization, execution, and error wrapping

Use case behavior:
- Trims input; rejects blank input with `ValidationError("Enter a SQL statement.")`
- Appends `;` if missing
- Wraps thrown exceptions into `ExecutionError` with `throwable.message ?: "Console command failed."`

### feature:console:data
- `SqlDelightConsoleCommandExecutor` — implements `ConsoleCommandExecutor` using `SafeRepo.execute()`
- Queries returning a non-null value produce `OUTPUT` + `STATUS("Query returned 1 row(s).")`
- Statements returning null produce `STATUS("Statement executed successfully.")`

### feature:console:presentation
- `ConsoleViewModel` with `ConsoleResult` / `ConsoleAction` MVI pattern
- Manages `input`, `running`, `transcript`, and `commandHistory` state
- Session-only history (not persisted across restarts)
- Ignores submit while already running

### feature:console:ui
Custom Compose-Multiplatform terminal surface (Android/JVM/iOS/wasmJs).
- `ConsoleSurface` — public composable; the body of the console screen
- `ConsoleBuffer` / `ConsoleBufferBuilder` — read-only scrollback model
- `ConsolePromptVisualTransformation` — adds `    ...> ` continuation prompt after every `\n` in the input
- `ConsoleStatementAnalyzer` — heuristic completeness check (trim → empty false → `.` prefix true → `;` suffix true)
- `ConsoleTheme` / `rememberConsoleTheme()` — Material3-derived palette and typography (no hardcoded colors)
- Internal renderers in `render/`: `ConsoleHistoryCanvas` (Canvas + `TextMeasurer`) and `ConsoleInputRow` (`BasicTextField` + Run button)

The module is resource-free — the surrounding `ConsoleScreen` in `:ui:shared` resolves strings via `stringResource(...)` and passes them as plain parameters. See `feature/console/ui/README.md` for design rationale and roadmap.

## Limitations

`SafeRepo.execute()` returns only the first column of the first row as `String?`. Full result sets are not available through the console.

## UI

The `ConsoleScreen` composable lives in `ui:shared` alongside other settings detail screens. It owns the Scaffold/TopAppBar, tips dropdown, helper text, progress indicator, and Koin view-model wiring; the terminal body itself is delegated to `ConsoleSurface` from `:feature:console:ui`. Together they provide:
- Top app bar with "Console tips" overflow menu
- A custom Canvas-rendered scrollback with theme-aware colors
- Multi-line input with `sqlite>` prompt and `    ...> ` continuation prompt
- Run button preserves the existing test contract

## Testing

```bash
# Domain and data unit tests
./gradlew :feature:console:domain:allTests
./gradlew :feature:console:data:allTests

# ViewModel tests
./gradlew :feature:console:presentation:allTests

# UI module unit tests (buffer, statement analyzer, visual transformation)
./gradlew :feature:console:ui:jvmTest

# Compose UI tests (ConsoleScreen)
./gradlew :ui:test-jvm:jvmTest

# Full build
./gradle/build_quick.sh
```
