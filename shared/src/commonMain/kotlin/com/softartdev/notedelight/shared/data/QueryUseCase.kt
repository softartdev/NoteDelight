package com.softartdev.notedelight.shared.data

import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.db.NoteQueries
import com.squareup.sqldelight.Query

class QueryUseCase(
    val noteQueries: NoteQueries
) {
    //intended for use from view models in Swift
    @Throws(Exception::class) fun loadNotes(): List<Note> {
        return noteQueries.getAll().executeAsList()
    }

    fun launchNotes(
        onSuccess: (List<Note>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = try {
        val query = noteQueries.getAll()
        val listener = object : Query.Listener {
            override fun queryResultsChanged() {
                onSuccess(query.executeAsList())
            }
        }
        query.addListener(listener)
        onSuccess(query.executeAsList())
    } catch (t: Throwable) {
        t.printStackTrace()
        onFailure(t)
    }

    @Throws(Exception::class) fun addNote(title: String = "", text: String = ""): Long {
        val notes = loadNotes()
        val noteId = if (notes.isEmpty()) 1 else notes.last().id + 1
        val localDateTime = createLocalDateTime()
        val note = Note(noteId, title, text, localDateTime, localDateTime)
        noteQueries.insert(note)
        return noteId
    }

    @Throws(Exception::class) suspend fun saveNote(id: Long, title: String, text: String): Note {
        val note = loadNote(id).copy(
            title = title,
            text = text,
            dateModified = createLocalDateTime()
        )
        noteQueries.update(note)
        return loadNote(id)
    }

    @Throws(Exception::class) suspend fun loadNote(noteId: Long): Note = noteQueries.getById(noteId).executeAsOne()

    fun deleteNote(id: Long) = noteQueries.delete(id)
}