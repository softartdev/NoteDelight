# NoteDelight Roadmap 🗺️

This document outlines the strategic plan, architectural considerations, and prioritized milestones for future NoteDelight development.

---

## 🎯 Release Milestones Overview

| Milestone | Focus | Key Features | Status |
| :--- | :--- | :--- | :--- |
| **v1.1** | **Performance & List Usability** | Note list SQL projection (`NoteHeader`), Note sorting (✅), Pin to top | 🚧 In Progress |
| **v1.2** | **Inter-App Sharing & Integration** | Share export (`ShareInteractor`), Share import (`ACTION_SEND`), Encrypted pending state | ✅ Completed |
| **v1.3** | **Search & Content Formatting** | Full-text / local search, basic Markdown support, Single-note export/import (.md/.txt) | 💡 Backlog |
| **v1.4** | **Security & Safety Enhancements** | Trash / Soft delete, Auto-lock background timer | 💡 Backlog |

---

## 🚀 Milestone Details

### Milestone 1.1: Performance & List Usability

#### 1. Note List Memory Optimization (`NoteHeader` Projection)
* **Problem:** The current query (`SELECT * FROM note`) retrieves the entire note body (`text`), which can be large, for all rows when loading the paged list. This retains unnecessary text blobs in heap memory and forces SQLite to load overflow pages.
* **Architecture & Solution:**
  * Define a lightweight domain model [`NoteHeader`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/domain/src/commonMain/kotlin/com/softartdev/notedelight/model/Note.kt):
    ```kotlin
    data class NoteHeader(
        val id: Long,
        val title: String,
        val dateCreated: LocalDateTime,
        val dateModified: LocalDateTime,
    )
    ```
  * In SQLDelight ([`Note.sq`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/data/db-sqldelight/src/commonMain/sqldelight/com/softartdev/notedelight/shared/db/Note.sq)):
    ```sql
    pagedHeaders:
    SELECT id, title, dateCreated, dateModified FROM note ORDER BY dateModified DESC LIMIT :limit OFFSET :offset;
    ```
  * In Room ([`NoteRoomDao`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/data/db-room/src/commonMain/kotlin/com/softartdev/notedelight/db/Database.kt)): define a Room projection DTO and update the paging source.
  * In UI ([`NoteItem.kt`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/ui/src/commonMain/kotlin/com/softartdev/notedelight/ui/main/NoteItem.kt)): bind to `NoteHeader` instead of full `Note`.
  * The full note text continues to be retrieved on-demand via `noteDAO.load(id)` only when opening [`NoteDetail`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/ui/src/commonMain/kotlin/com/softartdev/notedelight/ui/main/NoteDetail.kt).

#### 2. Note Sorting ✅
* **Domain & Reactive Data Layer:**
  * [`SortField`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/domain/src/commonMain/kotlin/com/softartdev/notedelight/model/SortField.kt) (`DATE_MODIFIED`, `DATE_CREATED`, `TITLE` holding SQL `columnName`) and [`SortOrder`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/domain/src/commonMain/kotlin/com/softartdev/notedelight/model/SortOrder.kt) (`ASC`, `DESC` holding SQL `sql`) compose [`NoteSort`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/domain/src/commonMain/kotlin/com/softartdev/notedelight/model/NoteSort.kt), orchestrated by [`NoteSortUseCase`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/domain/src/commonMain/kotlin/com/softartdev/notedelight/usecase/note/NoteSortUseCase.kt) without touching [`SafeRepo`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/domain/src/commonMain/kotlin/com/softartdev/notedelight/repository/SafeRepo.kt).
  * Non-breaking reactive [`NoteDAO`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/domain/src/commonMain/kotlin/com/softartdev/notedelight/db/NoteDAO.kt) API: `pagingDataFlow` switches automatically via `noteSortFlow.flatMapLatest`, executing dynamic raw SQL `ORDER BY ${sort.field.columnName} COLLATE NOCASE ${sort.order.sql}, id DESC` in both SQLDelight ([`SqlDelightNoteDAO.kt`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/data/db-sqldelight/src/commonMain/kotlin/com/softartdev/notedelight/db/SqlDelightNoteDAO.kt)) and Room (`@RawQuery` in [`Database.kt`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/data/db-room/src/commonMain/kotlin/com/softartdev/notedelight/db/Database.kt) / [`RoomNoteDAO.kt`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/data/db-room/src/commonMain/kotlin/com/softartdev/notedelight/db/RoomNoteDAO.kt)).
  * *Limitation:* Title sorting uses SQLite's built-in `COLLATE NOCASE`, which folds ASCII (`A-Z` / `a-z`) case-insensitively; non-ASCII (e.g. Cyrillic) characters sort by UTF-8 codepoint order unless a custom Unicode collator is registered across drivers.
* **UI:** [`SortMenuButton`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/ui/src/commonMain/kotlin/com/softartdev/notedelight/ui/main/SortMenuButton.kt) and [`SortOption`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/ui/src/commonMain/kotlin/com/softartdev/notedelight/ui/main/SortOption.kt) with `Icons.AutoMirrored.Filled.Sort` in [`MainScreen`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/ui/src/commonMain/kotlin/com/softartdev/notedelight/ui/main/MainScreen.kt) top app bar actions with 6 options:
  * By Last Modified Date (Descending *default* / Ascending)
  * By Creation Date (Descending / Ascending)
  * By Title (A–Z / Z–A)
* **Smart Date Display:** [`NoteItem.kt`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/ui/src/commonMain/kotlin/com/softartdev/notedelight/ui/main/NoteItem.kt) (`NoteItemDates`) displays a single creation timestamp with `Icons.Default.CalendarToday` when the note has not been modified after creation, or both `dateCreated` (`Icons.Default.AddCircleOutline`) and `dateModified` (`Icons.Default.Edit`) side-by-side when modified.
* **Persistence:** Persisted across app restarts via [`PreferencesRepo`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/domain/src/commonMain/kotlin/com/softartdev/notedelight/repository/PreferencesRepo.kt) / [`PreferencesRepoImpl`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/presentation/src/commonMain/kotlin/com/softartdev/notedelight/repository/PreferencesRepoImpl.kt) backed by `multiplatform-settings`.

#### 3. Pin to Top / Favorite Notes
* **Data Layer:** Add `isPinned: Boolean` flag in SQLite schema with migration.
* **Behavior:** Pinned notes stay at the top of the list irrespective of the active sort order, with a distinct visual pin indicator in [`NoteItem.kt`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/ui/src/commonMain/kotlin/com/softartdev/notedelight/ui/main/NoteItem.kt).

---

### Milestone 1.2: Inter-App Sharing & Integration

#### 1. Share Export (Outbound)
* **UI:** Add a "Share" action icon in [`NoteDetailBody`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/ui/src/commonMain/kotlin/com/softartdev/notedelight/ui/main/NoteDetail.kt) top app bar (`Icons.Default.Share`).
* **Platform Abstraction:** Implement [`ShareInteractor`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/presentation/src/commonMain/kotlin/com/softartdev/notedelight/interactor/ShareInteractor.kt) in `:core:presentation`:
  * Android: fires `Intent(Intent.ACTION_SEND)` with `ClipDescription.MIMETYPE_TEXT_PLAIN` via `Intent.createChooser()`.
  * iOS: presents `UIActivityViewController` (with `popoverPresentationController` support for iPad).
  * Web: calls `navigator.share()` with copyable snackbar fallback via `SnackbarMessage.Copyable`.
  * Desktop: shows copyable snackbar feedback via `SnackbarMessage.Copyable`.

#### 2. Share Import (Inbound) & Encrypted DB Flow
* **Inbound Handler:**
  * Android: Configure `android:launchMode="singleTop"` and `<intent-filter>` for `android.intent.action.SEND` with MIME type `text/plain` in [`AndroidManifest.xml`](file:///Users/artur/AndroidStudioProjects/NoteDelight/app/android/src/main/AndroidManifest.xml) for [`MainActivity`](file:///Users/artur/AndroidStudioProjects/NoteDelight/app/android/src/main/java/com/softartdev/notedelight/MainActivity.kt).
  * iOS: Provide a `ShareExtension` target (`com.apple.share-services`, [`ShareViewController.swift`](file:///Users/artur/AndroidStudioProjects/NoteDelight/app/iosApp/ShareExtension/ShareViewController.swift)) with shared App Group `group.com.softartdev.notedelight`, plus `CFBundleDocumentTypes` (`public.plain-text`) in [`iOSApp.swift`](file:///Users/artur/AndroidStudioProjects/NoteDelight/app/iosApp/iosApp/iOSApp.swift) forwarding incoming text via [`IosAppLauncher.onSharedText`](file:///Users/artur/AndroidStudioProjects/NoteDelight/app/ios-kit/src/commonMain/kotlin/com/softartdev/notedelight/IosAppLauncher.kt#L35).
  * Hold incoming text in memory inside [`ShareInteractor`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/presentation/src/commonMain/kotlin/com/softartdev/notedelight/interactor/ShareInteractor.kt) (`sharedTextStateFlow`).
* **Security & Immediate Persistence Strategy:**
  * **When DB is encrypted:** SQLite operations are blocked until the user authenticates on [`SignInScreen`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/ui/src/commonMain/kotlin/com/softartdev/notedelight/ui/signin/SignInScreen.kt). The text is safely retained in `ShareInteractor` while `AdaptiveMainScreen` is not yet composed.
  * **Upon DB unlock (or if unencrypted):** When [`AdaptiveMainScreen`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/ui/src/commonMain/kotlin/com/softartdev/notedelight/ui/main/AdaptiveMainScreen.kt) enters composition and starts [`MainViewModel`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/presentation/src/commonMain/kotlin/com/softartdev/notedelight/presentation/main/MainViewModel.kt) (or when new shared text arrives while `MainViewModel` is active), consume `sharedTextStateFlow`, create and persist the note in SQLite using [`CreateNoteUseCase`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/domain/src/commonMain/kotlin/com/softartdev/notedelight/usecase/note/CreateNoteUseCase.kt#L10), and navigate to the detail pane via `MainAction.OnNoteClick(noteId)`.
  * **Rationale:** Immediate saving guarantees no data loss if the app process is terminated or backgrounded, keeps encrypted data secure, and seamlessly aligns with NoteDelight's adaptive list-detail navigation architecture.

---

### Milestone 1.3: Search & Content Formatting

#### 1. Note Search (Local / Full-Text Search)
* Add a search bar or expandable search action in [`MainScreen`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/ui/src/commonMain/kotlin/com/softartdev/notedelight/ui/main/MainScreen.kt).
* Filter notes by title and content using SQLite queries (or evaluate SQLite `FTS5` virtual table indexing in SQLCipher).

#### 2. Basic Markdown Support
* Render headings, lists, bold/italics, and interactive checklist items in the note editor and preview mode.

#### 3. Single-Note File Export & Import (.md / .txt)
* Allow exporting individual notes as plaintext `.txt` or `.md` files to device storage.
* Allow opening and importing `.txt` / `.md` files directly into a new note.

---

### Milestone 1.4: Security & Data Safety

#### 1. Soft Delete & Trash (Recycle Bin)
* Instead of immediate permanent deletion (`DELETE FROM note WHERE id = :id`), mark deleted notes with `isDeleted = true` / `deletedAt`.
* Provide a "Trash" view under Settings to view, restore, or permanently purge deleted notes.

#### 2. Background Auto-Lock Timer
* Configurable timeout (e.g. Immediately, 1 min, 5 min, 15 min) in Security Settings.
* When NoteDelight goes to the background and the timeout expires, purge the decrypted database connection from memory and require password / biometric re-authentication on resume.

---

## 📌 Feature Roadmap Matrix

| Feature | Target Milestone | Domain / Data Layer | Presentation / UI Layer | Platforms Affected |
| :--- | :---: | :--- | :--- | :--- |
| **Note List Memory Optimization** | v1.1 | `NoteHeader`, SQLDelight query, Room DAO | `MainViewModel`, `NoteItem` | All |
| **Note Sorting** ✅ | v1.1 | `SortField`, `SortOrder`, `NoteSort`, `NoteSortUseCase`, `PreferencesRepo`, reactive `NoteDAO.noteSortFlow` | `MainScreen`, `SortMenuButton`, `SortOption`, `NoteItem`, `PreferencesRepoImpl` | All |
| **Pin to Top** | v1.1 | Schema `isPinned`, queries | `NoteItem`, `MainViewModel` | All |
| **Share Outbound** | v1.2 | `ShareInteractor` contract | `NoteDetail`, `NoteViewModel`, platform share handlers | Android, iOS, Desktop, Web |
| **Share Inbound** | v1.2 | `ShareInteractor`, `CreateNoteUseCase` | `MainActivity`, `MainViewModel` | Android (primary) |
| **Search / FTS** | v1.3 | FTS5 / SQL queries | `MainViewModel`, `MainScreen` search bar | All |
| **Markdown Editor** | v1.3 | Text formatting parser | `NoteDetail`, `NoteDetailBody` | All |
| **Single-Note Export/Import** | v1.3 | `FileRepo` / Document file picker | `NoteDetail` menu | All |
| **Trash (Soft Delete)** | v1.4 | `isDeleted` schema flag, cleanup job | Settings / Trash UI | All |
| **Auto-Lock Timer** | v1.4 | Lifecycle observer, `SafeRepo` close | App lifecycle, Security settings | All |

---

## 🛠️ Maintenance & Technical Debt

* **Remove LeakCanary from Android Debug Builds:**
  * Remove `leakcanary-android`, `plumber-android`, and `leakcanary-android-instrumentation` from [`app/android/build.gradle.kts`](file:///Users/artur/AndroidStudioProjects/NoteDelight/app/android/build.gradle.kts) and [`gradle/libs.versions.toml`](file:///Users/artur/AndroidStudioProjects/NoteDelight/gradle/libs.versions.toml).
  * *Rationale:* `leakcanary-android` merges `android.permission.POST_NOTIFICATIONS` and `leakcanary.internal.RequestPermissionActivity` into debug builds (triggering `"Allow Note Delight to send you notifications?"` prompts), while Jetpack Compose UI and Android Studio's built-in Memory Profiler make runtime LeakCanary overhead redundant.
* **Refactor `SafeRepo.relaunchListFlowCallback` out of `MainViewModel.init`:**
  * Replace the mutable callback assignment (`safeRepo.relaunchListFlowCallback = this::updateNotes`) in [`MainViewModel`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/presentation/src/commonMain/kotlin/com/softartdev/notedelight/presentation/main/MainViewModel.kt) / [`SafeRepo`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/domain/src/commonMain/kotlin/com/softartdev/notedelight/repository/SafeRepo.kt) with a reactive signal or dedicated use-case orchestration when database encryption, decryption, rekeying, or import resets the active SQLite connection.
* **Pre-formatted Date Strings at Query / Mapping Stage (Presentation / Domain Model):**
  * Shift date-to-text formatting (`DateTimeFormatter.format(...)`) from the Compose composition phase in [`NoteItem.kt`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/ui/src/commonMain/kotlin/com/softartdev/notedelight/ui/main/NoteItem.kt) to the database query or presentation mapping layer (e.g., inside `NoteHeader` or a dedicated presentation list item model) so that the model already contains pre-computed display strings.
  * *Rationale:* Avoids repetitive date parsing and string formatting during Compose list scrolling and recompositions, supplying the UI with ready-to-render display strings directly.
