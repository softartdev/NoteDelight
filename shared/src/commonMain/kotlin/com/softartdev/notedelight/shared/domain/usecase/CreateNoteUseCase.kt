package com.softartdev.notedelight.shared.domain.usecase

import com.softartdev.notedelight.shared.domain.model.Note
import com.softartdev.notedelight.shared.domain.repository.NoteDAO
import kotlinx.datetime.Clock

class CreateNoteUseCase(private val noteDAO: NoteDAO) {

    operator fun invoke(): Long {
        val note = Note(
            id = 0L,
            title = "",
            text = "",
            dateCreated = Clock.System.now().toLocalDateTime(),
            dateModified = Clock.System.now().toLocalDateTime()
        )
        return noteDAO.insert(note)
    }
}
