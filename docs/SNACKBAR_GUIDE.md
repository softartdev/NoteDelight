# Snackbar Handling Guide

This guide explains the shared snackbar flow that powers every platform.

## Core Components

- `core/presentation/src/commonMain/kotlin/com/softartdev/notedelight/interactor/SnackbarInteractor.kt` – multiplatform contract with `setDependencies`, `releaseDependencies`, and `showMessage` returning a `Job?`.
- `ui/shared/src/commonMain/kotlin/com/softartdev/notedelight/interactor/SnackbarInteractorImpl.kt` – Compose-aware implementation that wires a `SnackbarHostState`, clipboard access, and coroutine scope.
- `ui/shared/src/commonMain/kotlin/com/softartdev/notedelight/ui/GlobalSnackbarHost.kt` – composable that installs a global `SnackbarHost` and feeds the implementation via `DisposableEffect`.
- `ui/shared/src/commonMain/kotlin/com/softartdev/notedelight/App.kt` – registers the host at the root and injects `SnackbarInteractor` through Koin.

Keep the interface free from Compose types so it can be used in tests and on non-Compose platforms.

## Message Types

`SnackbarMessage` is a sealed interface designed for AI-facing ergonomics:

- `Simple(text: String)` – fire-and-forget message.
- `Copyable(text: String)` – shows a `Copy` action and writes to the clipboard when the action is triggered.
- `Resource(res: SnackbarTextResource, suffix: String = "")` – resolves a string resource (`SAVED`, `EMPTY`, `DELETED`) and optionally appends context text.

If you need a new reusable message, extend `SnackbarTextResource` instead of duplicating strings.

## Lifecycle

1. `GlobalSnackbarHost` creates a host state, clipboard, and coroutine scope, then calls `setDependencies` on the interactor.
2. `SnackbarInteractorImpl.showMessage` launches work on that scope, routing messages to the host and clipboard.
3. When the host leaves composition `releaseDependencies` clears references, preventing leaks on iOS/desktop.

Do **not** re-create or inject `SnackbarHostState` elsewhere—always rely on the global host.

## Calling from ViewModels

- Inject `SnackbarInteractor` in the ViewModel constructor (see Koin registrations in `ui/shared/di/uiModules.kt`).
- Call `snackbarInteractor.showMessage(...)` directly; the coroutine is launched inside the interactor so `viewModelScope.launch` is unnecessary unless you need structured cancellation.
- Optionally keep the returned `Job` if you need to cancel an in-flight snackbar before triggering another one.

Example usage inside `NoteViewModel`:

```kotlin
snackbarInteractor.showMessage(
    message = SnackbarMessage.Resource(
        res = SnackbarTextResource.SAVED,
        suffix = noteTitle,
    )
)
```

## Integration Checklist for AI Agents

- ✅ Add new snackbar entry points through the interactor, never by manipulating `SnackbarHostState` yourself.
- ✅ Use `SnackbarMessage.Resource` for translated strings—resources live in `ui/shared/src/commonMain/composeResources/values`.
- ✅ Keep UI wiring in `GlobalSnackbarHost`; screens should only consume the interactor.
- ⚠️ Remember to update tests: use a fake implementation of `SnackbarInteractor` rather than asserting on Compose state.
- ⚠️ Clean up any manual clipboard usage; `Copyable` already performs the write.
