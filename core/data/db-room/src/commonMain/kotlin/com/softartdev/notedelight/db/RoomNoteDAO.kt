package com.softartdev.notedelight.db

import androidx.paging.PagingSource
import com.softartdev.notedelight.model.Note
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
        get() = ModelPagingSource(delegate.pagingSource())

    override suspend fun load(id: Long): Note = delegate.load(id).model

    override suspend fun insert(note: Note) = delegate.insert(note.dbo)

    override suspend fun update(note: Note) = delegate.update(note.dbo)

    override suspend fun delete(id: Long) = delegate.delete(id)

    override suspend fun deleteAll() = delegate.deleteAll()
}
