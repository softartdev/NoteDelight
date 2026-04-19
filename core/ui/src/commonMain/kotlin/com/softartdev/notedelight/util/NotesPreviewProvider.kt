package com.softartdev.notedelight.util

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.softartdev.notedelight.db.TestSchema
import com.softartdev.notedelight.model.Note

class NotesPreviewProvider(
    override val values: Sequence<Note> = TestSchema.notes.asSequence()
) : PreviewParameterProvider<Note>
