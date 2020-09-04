package com.softartdev.notedelight.shared.data

import com.softartdev.notedelight.shared.database.DatabaseRepo
import com.softartdev.notedelight.shared.database.transactionResultWithContext
import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.softartdev.notedelight.shared.db.Note
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first

class NoteUseCase(
        private val dbRepo: DatabaseRepo
) {
    val titleChannel: Channel<String> by lazy { return@lazy Channel<String>() }

    fun doOnRelaunchFlow(function: () -> Unit) {
        dbRepo.relaunchFlowEmitter = function
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getNotes(): Flow<List<Note>> = dbRepo.noteQueries.getAll().asFlow().mapToList().distinctUntilChanged()

    suspend fun createNote(title: String = "", text: String = ""): Long {
        val lastInsertRowId = dbRepo.noteQueries.lastInsertRowId().executeAsOne()
        val notes = getNotes().first()
        val noteId = if (notes.isEmpty()) 1 else lastInsertRowId + 1
        val localDateTime = createLocalDateTime()
        val note = Note(noteId, title, text, localDateTime, localDateTime)
        dbRepo.noteQueries.insert(note)
        return noteId
    }

    suspend fun saveNote(id: Long, title: String, text: String): Int {
        val note = loadNote(id).copy(
                title = title,
                text = text,
                dateModified = createLocalDateTime()
        )
        dbRepo.noteQueries.update(note)
        return 1
    }

    suspend fun updateTitle(id: Long, title: String): Int {
        val note = loadNote(id).copy(
                title = title,
                dateModified = createLocalDateTime()
        )
        dbRepo.noteQueries.update(note)
        return 1
    }

    suspend fun loadNote(noteId: Long): Note = dbRepo.noteQueries.getById(noteId).asFlow().mapToOne().first()

    suspend fun deleteNote(id: Long): Int = dbRepo.noteQueries.transactionResultWithContext {
        val before = dbRepo.noteQueries.getAll().executeAsList().size
        dbRepo.noteQueries.delete(id)
        val after = dbRepo.noteQueries.getAll().executeAsList().size
        return@transactionResultWithContext before - after
    }

    suspend fun isChanged(id: Long, title: String, text: String): Boolean {
        val note = loadNote(id)
        return note.title != title || note.text != text
    }

    suspend fun isEmpty(id: Long): Boolean {
        val note = loadNote(id)
        return note.title.isEmpty() && note.text.isEmpty()
    }

}