package com.softartdev.notedelight.db

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.softartdev.notedelight.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the notes table.
 */
interface NoteDAO {
    /**
     * Select all notes from the notes table.
     *
     * @return all notes.
     */
    val listFlow: Flow<List<Note>>

    /**
     * Get a [PagingSource] for the notes table.
     */
    val pagingSource: PagingSource<Int, Note>

    /**
     * Get a [Flow] of [PagingData] for the notes table.
     */
    val pagingDataFlow: Flow<PagingData<Note>>
        get() = Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = this::pagingSource
        ).flow

    /**
     * Select the count of notes from the notes table.
     */
    suspend fun count(): Long

    /**
     * Select a note by id.
     *
     * @param id the note id.
     * @return the note with noteId.
     */
    suspend fun load(id: Long): Note

    /**
     * Insert a note in the database. If the note already exists, replace it.
     *
     * @param note the note to be inserted.
     */
    suspend fun insert(note: Note)

    /**
     * Update a note.
     *
     * @param note note to be updated
     */
    suspend fun update(note: Note)

    /**
     * Delete a note by id.
     *
     * @param id the note id.
     */
    suspend fun delete(id: Long)

    /**
     * Delete all notes.
     */
    suspend fun deleteAll()
}
