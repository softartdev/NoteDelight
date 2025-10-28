package com.softartdev.notedelight.util

import com.softartdev.notedelight.db.TestSchema
import com.softartdev.notedelight.model.Note
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class NotesPreviewProvider(
    override val values: Sequence<Note> = TestSchema.notes.asSequence()
) : PreviewParameterProvider<Note>
