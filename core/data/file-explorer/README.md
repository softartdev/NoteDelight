# core:data:file-explorer

Multiplatform data module that backs the in-app file explorer. It exposes the `FileRepo`
interface used by `FilesViewModel` and implements it with the help of Okio's `FileSystem`
on every supported platform (Android, JVM, iOS, and WASM).

## Responsibilities
- Track the current directory and map it to the UI friendly entries (`📂path`, `🔙..`, file icons).
- Expose the file tree through a cold `Flow<List<String>>` so presentation layers can observe
  navigations in real time.
- Provide safe helpers (`goToStartPath`, `goTo`) that keep users within the sandbox that is
  supported by each platform.
- Fall back gracefully when a platform cannot expose the file system (for example on WASM).

## Architecture
- `FileRepo` (in `core/domain`) defines the contract for fetching directories and navigating.
- `AbstractFileRepo` (commonMain) contains most of the logic: it routes clicks, reads files via
  `okio.FileSystem`, and updates a `MutableStateFlow` with human readable labels.
- Platform specific subclasses (`AndroidFileRepo`, `JvmFileRepo`, `IosFileRepo`, `WasmJsFileRepo`)
  inject the correct `FileSystem` instance and root `Path`.
- The module is wired through Koin and consumed by the shared UI inside the hidden File List screen.

## Testing
- Common unit tests live under `src/commonTest` and validate navigation and flow contracts.
- JVM/Android specific cases are covered by instrumentation tests in `core/presentation` that mock
  `FileRepo`.
- Run the full suite with `./gradlew :core:data:file-explorer:allTests` or rely on
  `./gradlew build`, which aggregates these tasks.

## Usage tips
- Always call `FileRepo.goToStartPath()` before expecting items from `fileListFlow` to ensure the
  repository resolved its initial directory.
- The explorer emits sentinel entries such as `🔙..` (navigate up) and `📖` (display file contents);
  make sure your UI filters or handles them explicitly.
- When adding a new platform implementation, extend `AbstractFileRepo` and override
  `fileSystem`/`zeroPath` instead of reimplementing navigation logic.
