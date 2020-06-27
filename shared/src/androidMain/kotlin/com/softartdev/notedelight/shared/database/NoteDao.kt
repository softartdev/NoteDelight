package com.softartdev.notedelight.shared.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    /**
     * Select all notes from the notes table.
     *
     * @return all notes.
     */
    @Query("SELECT * FROM note")
    fun getNotes(): Flow<List<Note>>

    /**
     * Select a note by id.
     *
     * @param noteId the note id.
     * @return the note with noteId.
     */
    @Query("SELECT * FROM note WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): Note

    /**
     * Insert a note in the database. If the note already exists, replace it.
     *
     * @param note the note to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    /**
     * Update a note.
     *
     * @param note note to be updated
     * @return the number of notes updated. This should always be 1.
     */
    @Update
    suspend fun updateNote(note: Note): Int

    /**
     * Delete a note by id.
     *
     * @return the number of notes deleted. This should always be 1.
     */
    @Query("DELETE FROM note WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: Long): Int

    /**
     * Delete all notes.
     */
    @Query("DELETE FROM note")
    suspend fun deleteNotes()

}