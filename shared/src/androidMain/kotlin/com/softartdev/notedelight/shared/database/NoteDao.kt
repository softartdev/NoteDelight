package com.softartdev.notedelight.shared.database

import kotlinx.coroutines.flow.Flow

interface NoteDao {

    /**
     * Select all notes from the notes table.
     *
     * @return all notes.
     */
    fun getNotes(): Flow<List<Note>>

    /**
     * Select a note by id.
     *
     * @param noteId the note id.
     * @return the note with noteId.
     */
    suspend fun getNoteById(noteId: Long): Note

    /**
     * Insert a note in the database. If the note already exists, replace it.
     *
     * @param note the note to be inserted.
     */
    suspend fun insertNote(note: Note): Long

    /**
     * Update a note.
     *
     * @param note note to be updated
     * @return the number of notes updated. This should always be 1.
     */
    suspend fun updateNote(note: Note): Int

    /**
     * Delete a note by id.
     *
     * @return the number of notes deleted. This should always be 1.
     */
    suspend fun deleteNoteById(noteId: Long): Int

    /**
     * Delete all notes.
     */
    suspend fun deleteNotes()

}