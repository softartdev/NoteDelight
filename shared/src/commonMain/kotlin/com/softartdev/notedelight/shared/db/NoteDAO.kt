package com.softartdev.notedelight.shared.db

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.PagingSource
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.paging3.QueryPagingSource
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
     * Get a [PagingSource] for the notes table.
     */
    val pagingSource: PagingSource<Int, Note>
        get() = QueryPagingSource(
            countQuery = noteQueries.countNotes(),
            transacter = noteQueries,
            context = Dispatchers.IO,
            queryProvider = noteQueries::pagedNotes,
        )

    /**
     * Get a [Flow] of [PagingData] for the notes table.
     */
    val pagingDataFlow: Flow<PagingData<Note>>
        get() = Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = this::pagingSource
        ).flow

    /**
     * Select a note by id.
     *
     * @param id the note id.
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