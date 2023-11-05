package com.softartdev.notedelight.shared.usecase.note

import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.db.NoteDAO
import kotlinx.coroutines.flow.first

class CreateNoteUseCase(private val noteDAO: NoteDAO) {

    suspend operator fun invoke(title: String = "", text: String = ""): Long {
        val notes = noteDAO.listFlow.first()
        val lastId: Long = notes.maxByOrNull(Note::id)?.id ?: 0
        val noteId = lastId + 1

        val localDateTime = createLocalDateTime()
        val note = Note(noteId, title, text, localDateTime, localDateTime)
        try {
            noteDAO.insert(note)
        } catch (cause: Throwable) {
            throw RuntimeException("Error create note with id = $noteId, lastId = $lastId", cause)
        }
        return noteId
    }
}
