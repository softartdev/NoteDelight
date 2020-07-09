package com.softartdev.notedelight.shared.database

import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.db.NoteQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow

class NoteDaoImpl(
    private val noteQueries: NoteQueries
) : NoteDao {

    override fun getNotes(): Flow<List<Note>> = noteQueries.getAll().asFlow().mapToList()

    override suspend fun getNoteById(noteId: Long): Note = noteQueries.getById(noteId).executeAsOne()

    override suspend fun insertNote(note: Note): Long {
        val noteId = if (note.id == 0L) {
            noteQueries.lastInsertRowId().executeAsOne() + 1
        } else note.id
        noteQueries.insert(note.copy(id = noteId))
        return noteId
    }

    override suspend fun updateNote(note: Note): Int {
        noteQueries.update(note)
        return 1
    }

    override suspend fun deleteNoteById(noteId: Long): Int {
        noteQueries.delete(noteId)
        return 1
    }

    override suspend fun deleteNotes() = noteQueries.deleteAll()
}