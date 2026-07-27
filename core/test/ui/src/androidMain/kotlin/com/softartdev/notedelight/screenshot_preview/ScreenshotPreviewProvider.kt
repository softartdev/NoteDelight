package com.softartdev.notedelight.screenshot_preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.PagingData
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.presentation.main.NoteListResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDateTime

class ScreenshotPreviewProvider(
    override val values: Sequence<NoteListResult.Success> = sequenceOf(
        NoteListResult.Success(
            result = MutableStateFlow(PagingData.from(notes)),
            selectedId = selected.id,
        )
    )
) : PreviewParameterProvider<NoteListResult.Success> {

    companion object {
        val notes: List<Note> = listOf(
            Note(
                id = 1,
                title = "Encrypted local database 🔐",
                text = """
                    Keep personal notes in an encrypted database instead of plain text files.

                    Your ideas, plans, drafts, and private checklists stay protected on the device you use.
                """.trimIndent(),
                dateCreated = LocalDateTime(2026, 4, 5, 9, 30),
                dateModified = LocalDateTime(2026, 5, 29, 18, 45),
            ),
            Note(
                id = 2,
                title = "Adaptive notes workspace ✨",
                text = """
                    Note Delight is a private notebook for the ideas, plans, and details you want to keep close.

                    It gives you a calm writing space with the practical tools a real notes app needs:

                    - encrypted storage for sensitive notes 🔐
                    - backup and restore when you move devices 💾
                    - biometric unlock on mobile for quick access 👆
                    - one Compose Multiplatform UI across phone, tablet, desktop, and web 🌍

                    Use it for travel plans, release notes, personal records, project ideas, and anything else that deserves to be easy to write down and hard to lose.
                """.trimIndent(),
                dateCreated = LocalDateTime(2026, 4, 18, 11, 15),
                dateModified = LocalDateTime(2026, 5, 30, 16, 10),
            ),
            Note(
                id = 3,
                title = "Backup and restore 💾",
                text = "Export a protected backup, keep it where you trust, and restore your notes when you move to a new device.",
                dateCreated = LocalDateTime(2026, 4, 20, 8, 0),
                dateModified = LocalDateTime(2026, 5, 26, 20, 5),
            ),
            Note(
                id = 4,
                title = "Biometric unlock 👆",
                text = "Open your encrypted notebook quickly on supported mobile devices without typing the password every time.",
                dateCreated = LocalDateTime(2026, 5, 2, 14, 20),
                dateModified = LocalDateTime(2026, 5, 22, 9, 35),
            ),
            Note(
                id = 5,
                title = "Android, iOS, Desktop, Web 🌍",
                text = "The same core app runs across mobile, desktop, and browser targets with a responsive Compose UI.",
                dateCreated = LocalDateTime(2026, 5, 8, 19, 40),
                dateModified = LocalDateTime(2026, 5, 17, 12, 25),
            ),
        )
        val selected: Note = notes[1]
    }
}
