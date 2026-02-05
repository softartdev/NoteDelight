# feature:backup

Backup feature modules that handle database import/export and user-facing file selection.

## Modules

- `feature:backup:domain`
  - Database backup use cases (`ExportDatabaseUseCase`, `ImportDatabaseUseCase`).
  - `DatabaseFileTransfer` expect/actual singleton for platform-specific file copying.
  - Uses a shared non-WASM source set for Okio-based file transfer.
- `feature:backup:ui`
  - `DatabaseFilePicker` interface and platform file picker implementations.
  - Test-friendly injection via `TestDatabaseFilePicker`.

## Notes

- Non-WASM targets use Okio-based transfer; WASM throws `UnsupportedOperationException`.
