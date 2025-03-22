package com.softartdev.notedelight.db

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
