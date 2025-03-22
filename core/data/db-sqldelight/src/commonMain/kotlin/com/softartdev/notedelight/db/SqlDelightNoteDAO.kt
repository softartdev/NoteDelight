package com.softartdev.notedelight.db

import app.cash.paging.PagingSource
import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.paging3.QueryPagingSource
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.shared.db.NoteQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import com.softartdev.notedelight.shared.db.Note as NoteDBO

class SqlDelightNoteDAO(private val noteQueries: NoteQueries) : NoteDAO {

    override val listFlow: Flow<List<Note>>
        get() = noteQueries.getAll().asFlow()
            .mapToList(Dispatchers.IO)
            .map(List<NoteDBO>::toModel)
            .distinctUntilChanged()

    override val count: Long
        get() = noteQueries.countNotes().executeAsOne()

    override val pagingSource: PagingSource<Int, Note>
        get() = QueryPagingSource(
            countQuery = noteQueries.countNotes(),
            transacter = noteQueries,
            context = Dispatchers.IO,
            queryProvider = this::pagedNotes
        )

    private fun pagedNotes(limit: Long, offset: Long): Query<Note> =
        noteQueries.pagedNotes(limit, offset).toModel()

    override fun load(id: Long): Note = noteQueries.getById(noteId = id).executeAsOne().model

    override fun insert(note: Note) = noteQueries.insert(note.dbo)

    override fun update(note: Note) = noteQueries.update(note.dbo)

    override fun delete(id: Long) = noteQueries.delete(noteId = id)

    override fun deleteAll() = noteQueries.deleteAll()
}