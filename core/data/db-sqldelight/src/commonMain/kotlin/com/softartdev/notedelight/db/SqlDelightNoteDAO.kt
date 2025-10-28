package com.softartdev.notedelight.db

import androidx.paging.PagingSource
import app.cash.sqldelight.Query
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.paging3.QueryPagingSource
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.shared.db.NoteQueries
import com.softartdev.notedelight.util.CoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import com.softartdev.notedelight.shared.db.Note as NoteDBO

class SqlDelightNoteDAO(
    private val noteQueriesGetter: () -> NoteQueries,
    private val coroutineDispatchers: CoroutineDispatchers
) : NoteDAO {
    private val noteQueries: NoteQueries
        get() = noteQueriesGetter()

    override val listFlow: Flow<List<Note>>
        get() = noteQueries.getAll().asFlow()
            .mapToList(context = coroutineDispatchers.io)
            .map(transform = List<NoteDBO>::toModel)
            .distinctUntilChanged()

    override val pagingSource: PagingSource<Int, Note>
        get() = QueryPagingSource(
            countQuery = noteQueries.countNotes(),
            transacter = noteQueries,
            context = coroutineDispatchers.io,
            queryProvider = this::pagedNotes
        )

    private fun pagedNotes(limit: Long, offset: Long): Query<Note> =
        noteQueries.pagedNotes(limit, offset).toModel()

    override suspend fun count(): Long = noteQueries.countNotes().awaitAsOne()

    override suspend fun load(id: Long): Note = noteQueries.getById(noteId = id).awaitAsOne().model

    override suspend fun insert(note: Note) {
        noteQueries.insert(note.dbo)
    }

    override suspend fun update(note: Note) {
        noteQueries.update(note.dbo)
    }

    override suspend fun delete(id: Long) {
        noteQueries.delete(noteId = id)
    }

    override suspend fun deleteAll() {
        noteQueries.deleteAll()
    }
}