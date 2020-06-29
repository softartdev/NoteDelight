package com.softartdev.notedelight.shared.db

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

class PlatformNoteUseCase(
    private val platformRepo: PlatformRepo
) {
    val titleChannel: Channel<String> by lazy { return@lazy Channel<String>() }

    fun doOnRelaunchFlow(function: () -> Unit) {
        platformRepo.relaunchFlowEmitter = function
    }

    @ExperimentalCoroutinesApi
    fun getNotes(): Flow<List<Note>> = platformRepo.noteQueries.getAll()
        .asFlow()
        .mapToList()
        .distinctUntilChanged()

    suspend fun createNote(title: String = "", text: String = ""): Long {
        val date = Date()
        val note = Note(0, title, text, date, date)
        platformRepo.noteQueries.insert(note)
        return platformRepo.noteQueries.lastInsertRowId().asFlow().mapToOne().single()
    }

    suspend fun saveNote(id: Long, title: String, text: String) {
        val note = platformRepo.noteQueries.getById(id).asFlow().mapToOne().single().copy(
            title = title,
            text = text,
            dateModified = Date()
        )
        return platformRepo.noteQueries.update(note)
    }

    suspend fun updateTitle(id: Long, title: String) {
        val note = platformRepo.noteQueries.getById(id).asFlow().mapToOne().single().copy(
            title = title,
            dateModified = Date()
        )
        return platformRepo.noteQueries.update(note)
    }

    suspend fun loadNote(id: Long): Note =
        platformRepo.noteQueries.getById(id).asFlow().mapToOne().single()

    suspend fun deleteNote(id: Long) = withContext(EmptyCoroutineContext) {
        platformRepo.noteQueries.delete(id)
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