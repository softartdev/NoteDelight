package com.softartdev.notedelight.shared.usecase.note

import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.softartdev.notedelight.shared.db.NoteDAO

class SaveNoteUseCase(private val noteDAO: NoteDAO) : (Long, String, String) -> Unit {

    override fun invoke(id: Long, title: String, text: String) {
        val note = noteDAO.load(id).copy(
            title = title,
            text = text,
            dateModified = createLocalDateTime()
        )
        noteDAO.update(note)
    }
}
