package com.softartdev.notedelight.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.model.Note as NoteModel

val Note.model: NoteModel
    get() = NoteModel(
        id = id,
        title = title,
        text = text,
        dateCreated = dateCreated,
        dateModified = dateModified,
    )

val NoteModel.dbo: Note
    get() = Note(
        id = id,
        title = title,
        text = text,
        dateCreated = dateCreated,
        dateModified = dateModified,
    )

fun List<Note>.toModel(): List<NoteModel> = map(Note::model)

fun List<NoteModel>.toDBO(): List<Note> = map(NoteModel::dbo)

fun Query<Note>.toModel(): Query<NoteModel> = object : Query<NoteModel>(
    mapper = { cursor: SqlCursor -> this@toModel.mapper(cursor).model }
) {
    override fun addListener(listener: Listener) = this@toModel.addListener(listener)

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        this@toModel.execute(mapper)

    override fun removeListener(listener: Listener) = this@toModel.removeListener(listener)
}
