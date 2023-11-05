package com.softartdev.notedelight.shared.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Data Access Object for the notes table.
 */
class NoteDAO(private val noteQueries: NoteQueries) {
    /**
     * Select all notes from the notes table.
     *
     * @return all notes.
     */
    val listFlow: Flow<List<Note>>
        get() = noteQueries.getAll().asFlow().mapToList(Dispatchers.IO).distinctUntilChanged()

    /**
     * Select a note by id.
     *
     * @param noteId the note id.
     * @return the note with noteId.
     */
    fun load(id: Long): Note = noteQueries.getById(noteId = id).executeAsOne()

    /**
     * Insert a note in the database. If the note already exists, replace it.
     *
     * @param note the note to be inserted.
     */
    fun insert(note: Note) = noteQueries.insert(note)

    /**
     * Update a note.
     *
     * @param note note to be updated
     */
    fun update(note: Note) = noteQueries.update(note)

    /**
     * Delete a note by id.
     *
     * @param id the note id.
     */
    fun delete(id: Long) = noteQueries.delete(noteId = id)

    /**
     * Delete all notes.
     */
    fun deleteAll() = noteQueries.deleteAll()
}