# NoteDelight Roadmap 🗺️

This document outlines the strategic plan, architectural considerations, and prioritized milestones for future NoteDelight development.

---

## 🎯 Release Milestones Overview

| Milestone | Focus | Key Features | Status |
| :--- | :--- | :--- | :--- |
| **v1.1** | **Performance & List Usability** | Note list SQL projection (`NoteHeader`), Note sorting, Pin to top | 📋 Planned |
| **v1.2** | **Inter-App Sharing & Integration** | Share export (`ShareInteractor`), Share import (`ACTION_SEND`), Encrypted pending state | 📋 Planned |
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

#### 2. Note Sorting
* **UI:** Add a Sort action with `Icons.AutoMirrored.Filled.Sort` in [`MainScreen`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/ui/src/commonMain/kotlin/com/softartdev/notedelight/ui/main/MainScreen.kt) top app bar actions with a dropdown menu:
  * By Last Modified Date (*default: Descending*)
  * By Creation Date (Descending / Ascending)
  * By Title (A-Z / Z-A)
* **Dynamic Date Display:** Update [`NoteItem.kt`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/ui/src/commonMain/kotlin/com/softartdev/notedelight/ui/main/NoteItem.kt):
  * When sorted by modification date, display `dateModified` in `overlineContent`.
  * When sorted by creation date, display `dateCreated` in `overlineContent`.
* **Persistence:** Persist selected sort order in settings/preferences so it survives app restarts.

#### 3. Pin to Top / Favorite Notes
* **Data Layer:** Add `isPinned: Boolean` flag in SQLite schema with migration.
* **Behavior:** Pinned notes stay at the top of the list irrespective of the active sort order, with a distinct visual pin indicator in [`NoteItem.kt`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/ui/src/commonMain/kotlin/com/softartdev/notedelight/ui/main/NoteItem.kt).

---

### Milestone 1.2: Inter-App Sharing & Integration

#### 1. Share Export (Outbound)
* **UI:** Add a "Share" action icon in [`NoteDetailBody`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/ui/src/commonMain/kotlin/com/softartdev/notedelight/ui/main/NoteDetail.kt) top app bar (`Icons.Default.Share`).
* **Platform Abstraction:** Implement `ShareInteractor` in `:core:presentation` (or `:core:domain`):
  * Android: fires `Intent(Intent.ACTION_SEND)` with `text/plain` via `Intent.createChooser()`.
  * iOS: presents `UIActivityViewController`.
  * Web: calls `navigator.share()` with clipboard fallback.
  * Desktop: copies note text to clipboard with snackbar feedback.

#### 2. Share Import (Inbound) & Encrypted DB Flow
* **Inbound Handler:**
  * Android: Configure `<intent-filter>` for `android.intent.action.SEND` with MIME type `text/plain` in [`AndroidManifest.xml`](file:///Users/artur/AndroidStudioProjects/NoteDelight/app/android/src/main/AndroidManifest.xml) for [`MainActivity`](file:///Users/artur/AndroidStudioProjects/NoteDelight/app/android/src/main/java/com/softartdev/notedelight/MainActivity.kt).
  * Hold incoming text in memory inside `ShareInteractor`:
    ```kotlin
    class ShareInteractor {
        var pendingIncomingText: String? = null
            private set
        fun setPendingIncomingText(text: String?) { pendingIncomingText = text }
        fun consumePendingIncomingText(): String? { ... }
    }
    ```
* **Security & Immediate Persistence Strategy:**
  * **When DB is encrypted:** SQLite operations are blocked until the user authenticates on [`SignInScreen`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/ui/src/commonMain/kotlin/com/softartdev/notedelight/ui/signin/SignInScreen.kt). The text is safely retained in `ShareInteractor`.
  * **Upon DB unlock (or if unencrypted):** Immediately create and persist the note in SQLite using [`CreateNoteUseCase(title, text)`](file:///Users/artur/AndroidStudioProjects/NoteDelight/core/domain/src/commonMain/kotlin/com/softartdev/notedelight/usecase/note/CreateNoteUseCase.kt#L10).
  * **Rationale:** Immediate saving guarantees no data loss if the app process is terminated or backgrounded, keeps encrypted data secure, and seamlessly aligns with NoteDelight's existing note creation architecture.
  * Clear pending text and navigate straight to editing the created note.

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
| **Note Sorting** | v1.1 | `NoteDAO` sort order parameter | `MainScreen`, `NoteItem`, Settings | All |
| **Pin to Top** | v1.1 | Schema `isPinned`, queries | `NoteItem`, `MainViewModel` | All |
| **Share Outbound** | v1.2 | `ShareInteractor` contract | `NoteDetail`, platform share handlers | Android, iOS, Desktop, Web |
| **Share Inbound** | v1.2 | `ShareInteractor`, `CreateNoteUseCase` | `MainActivity`, `SplashViewModel`, `SignInViewModel` | Android (primary), iOS |
| **Search / FTS** | v1.3 | FTS5 / SQL queries | `MainViewModel`, `MainScreen` search bar | All |
| **Markdown Editor** | v1.3 | Text formatting parser | `NoteDetail`, `NoteDetailBody` | All |
| **Single-Note Export/Import** | v1.3 | `FileRepo` / Document file picker | `NoteDetail` menu | All |
| **Trash (Soft Delete)** | v1.4 | `isDeleted` schema flag, cleanup job | Settings / Trash UI | All |
| **Auto-Lock Timer** | v1.4 | Lifecycle observer, `SafeRepo` close | App lifecycle, Security settings | All |
