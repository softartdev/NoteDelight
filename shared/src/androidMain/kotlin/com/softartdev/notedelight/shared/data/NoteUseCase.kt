package com.softartdev.notedelight.shared.data

import com.softartdev.notedelight.shared.database.SafeRepo
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first

class NoteUseCase(
        private val safeRepo: SafeRepo
) {
    val titleChannel: Channel<String> by lazy { return@lazy Channel<String>() }

    fun doOnRelaunchFlow(function: () -> Unit) {
        safeRepo.relaunchFlowEmitter = function
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getNotes(): Flow<List<Note>> = safeRepo.noteQueries.getAll().asFlow().mapToList().distinctUntilChanged()

    suspend fun createNote(title: String = "", text: String = ""): Long {
        val lastInsertRowId = safeRepo.noteQueries.lastInsertRowId().executeAsOne()
        val notes = getNotes().first()
        val noteId = if (notes.isEmpty()) 1 else lastInsertRowId + 1
        val localDateTime = createLocalDateTime()
        val note = Note(noteId, title, text, localDateTime, localDateTime)
        safeRepo.noteQueries.insert(note)
        return noteId
    }

    suspend fun saveNote(id: Long, title: String, text: String): Int {
        val note = loadNote(id).copy(
                title = title,
                text = text,
                dateModified = createLocalDateTime()
        )
        safeRepo.noteQueries.update(note)
        return 1
    }

    suspend fun updateTitle(id: Long, title: String): Int {
        val note = loadNote(id).copy(
                title = title,
                dateModified = createLocalDateTime()
        )
        safeRepo.noteQueries.update(note)
        return 1
    }

    suspend fun loadNote(noteId: Long): Note = safeRepo.noteQueries.getById(noteId).asFlow().mapToOne().first()

    suspend fun deleteNote(id: Long): Int {
        val before = getNotes().first().size
        safeRepo.noteQueries.delete(id)
        val after = getNotes().first().size
        return before - after
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