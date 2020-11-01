package com.softartdev.notedelight.shared.data

import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.database.transactionResultWithContext
import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.softartdev.notedelight.shared.db.Note
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class NoteUseCase(
        private val dbRepo: DatabaseRepo
) {
    val titleChannel: Channel<String> by lazy { return@lazy Channel<String>() }

    fun doOnRelaunchFlow(function: () -> Unit) {
        dbRepo.relaunchFlowEmitter = function
    }

    fun getNotes(): Flow<List<Note>> = dbRepo.noteQueries.getAll().asFlow().mapToList().distinctUntilChanged()

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
        val noteId = if (notes.isEmpty()) 1 else notes.last().id + 1
        val localDateTime = createLocalDateTime()
        val note = Note(noteId, title, text, localDateTime, localDateTime)
        dbRepo.noteQueries.insert(note)
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