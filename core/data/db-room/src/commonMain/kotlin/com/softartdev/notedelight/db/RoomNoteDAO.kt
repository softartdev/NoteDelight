package com.softartdev.notedelight.db

import androidx.paging.PagingSource
import androidx.room.RoomRawQuery
import com.softartdev.notedelight.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import com.softartdev.notedelight.db.Note as NoteDBO

class RoomNoteDAO(
    private val noteDatabase: NoteDatabase,
    private val delegate: NoteRoomDao = noteDatabase.noteDao()
) : NoteDAO {

    override val listFlow: Flow<List<Note>>
        get() = delegate.getNotes().map(List<NoteDBO>::toModel)

    override val count: Long
        get() = runBlocking { delegate.getCount() }

    override val pagingSource: PagingSource<Int, Note>
        get() = ModelLimitOffsetPagingSource(
            sourceQuery = RoomRawQuery(sql = "SELECT * FROM note ORDER BY dateModified DESC"),
            db = noteDatabase,
            "note"
        )

    override fun load(id: Long): Note = runBlocking { delegate.load(id).model }

    override fun insert(note: Note) = runBlocking { delegate.insert(note.dbo) }

    override fun update(note: Note) = runBlocking { delegate.update(note.dbo) }

    override fun delete(id: Long) = runBlocking { delegate.delete(id) }

    override fun deleteAll() = runBlocking { delegate.deleteAll() }
}