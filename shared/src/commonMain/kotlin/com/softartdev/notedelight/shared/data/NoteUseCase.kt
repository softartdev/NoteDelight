package com.softartdev.notedelight.shared.data

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.database.transactionResultWithContext
import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.softartdev.notedelight.shared.db.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class NoteUseCase(
        private val dbRepo: DatabaseRepo
) {
    val titleChannel: Channel<String> by lazy { return@lazy Channel<String>() }

    fun doOnRelaunchFlow(function: (() -> Unit)?) {
        dbRepo.relaunchFlowEmitter = function
    }

    fun getNotes(): Flow<List<Note>> = dbRepo.noteQueries.getAll().asFlow().mapToList(Dispatchers.IO).distinctUntilChanged()

    fun launchNotes(
        onSuccess: (List<Note>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) = try {
        val query = dbRepo.noteQueries.getAll()
        query.addListener(object : Query.Listener {
            override fun queryResultsChanged() {
                onSuccess(query.executeAsList())
            }
        })
        onSuccess(query.executeAsList())
    } catch (t: Throwable) {
        t.printStackTrace()
        onFailure(t)
    }

    @Throws(Exception::class) suspend fun createNote(title: String = "", text: String = ""): Long {
        val notes = dbRepo.noteQueries.getAll().executeAsList()
        val lastId: Long = notes.maxByOrNull(Note::id)?.id ?: 0
        val noteId = lastId + 1

        val localDateTime = createLocalDateTime()
        val note = Note(noteId, title, text, localDateTime, localDateTime)
        try {
            dbRepo.noteQueries.insert(note)
        } catch (cause: Throwable) {
            throw RuntimeException("Error create note with id = $noteId, lastId = $lastId", cause)
        }
        return noteId
    }

    @Throws(Exception::class) suspend fun saveNote(id: Long, title: String, text: String): Note {
        val note = loadNote(id).copy(
                title = title,
                text = text,
                dateModified = createLocalDateTime()
        )
        dbRepo.noteQueries.update(note)
        return note
    }

    suspend fun updateTitle(id: Long, title: String): Int {
        val note = loadNote(id).copy(
                title = title,
                dateModified = createLocalDateTime()
        )
        dbRepo.noteQueries.update(note)
        return 1
    }

    @Throws(Exception::class) suspend fun loadNote(noteId: Long): Note = dbRepo.noteQueries.getById(noteId).executeAsOne()

    suspend fun deleteNote(id: Long): Int = dbRepo.noteQueries.transactionResultWithContext {
        val before = dbRepo.noteQueries.getAll().executeAsList().size
        dbRepo.noteQueries.delete(id)
        val after = dbRepo.noteQueries.getAll().executeAsList().size
        return@transactionResultWithContext before - after
    }

    @Throws(Exception::class) suspend fun deleteNoteUnit(id: Long) = dbRepo.noteQueries.delete(id)

    suspend fun isChanged(id: Long, title: String, text: String): Boolean {
        val note = loadNote(id)
        return note.title != title || note.text != text
    }

    suspend fun isEmpty(id: Long): Boolean {
        val note = loadNote(id)
        return note.title.isEmpty() && note.text.isEmpty()
    }

}