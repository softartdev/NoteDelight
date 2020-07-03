package com.softartdev.notedelight.shared.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class NoteDaoImpl : NoteDao {

    private val notes = mutableListOf<Note>()

    override fun getNotes(): Flow<List<Note>> = flowOf(notes)

    override suspend fun getNoteById(noteId: Long): Note = notes.find { it.id == noteId }!!

    override suspend fun insertNote(note: Note): Long {
        val lastId = notes.lastOrNull()?.id ?: 0
        val noteId = lastId.inc()
        notes.add(note.copy(id = noteId))
        return noteId
    }

    override suspend fun updateNote(note: Note): Int {
        val update = notes.removeAll { it.id == note.id }
        notes.add(note)
        return if (update) 1 else 0
    }

    override suspend fun deleteNoteById(noteId: Long): Int {
        val delete = notes.removeAll { it.id == noteId }
        return if (delete) 1 else 0
    }

    override suspend fun deleteNotes() = notes.clear()
}