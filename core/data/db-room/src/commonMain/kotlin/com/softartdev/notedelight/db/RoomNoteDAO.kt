package com.softartdev.notedelight.db

import androidx.paging.PagingSource
import androidx.room3.RoomRawQuery
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.model.NoteSort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.softartdev.notedelight.db.Note as NoteDBO

class RoomNoteDAO(
    private val dbProvider: () -> NoteDatabase,
) : NoteDAO {

    private val delegate: NoteRoomDao
        get() = dbProvider().noteDao()

    override val listFlow: Flow<List<Note>>
        get() = delegate.getNotes().map(List<NoteDBO>::toModel)

    override suspend fun count(): Long = delegate.getCount()

    override val pagingSource: PagingSource<Int, Note>
        get() {
            val sort: NoteSort = NoteDAO.noteSortFlow.value
            val query = RoomRawQuery(
                sql = "SELECT * FROM note ORDER BY ${sort.field.columnName} COLLATE NOCASE ${sort.order.sql}, id DESC"
            )
            return ModelPagingSource(delegate.pagingSource(query))
        }

    override suspend fun load(id: Long): Note = delegate.load(id).model

    override suspend fun insert(note: Note) = delegate.insert(note.dbo)

    override suspend fun update(note: Note) = delegate.update(note.dbo)

    override suspend fun delete(id: Long) = delegate.delete(id)

    override suspend fun deleteAll() = delegate.deleteAll()
}
