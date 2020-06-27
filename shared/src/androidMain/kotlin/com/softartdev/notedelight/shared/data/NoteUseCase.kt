package com.softartdev.notedelight.shared.data

import com.softartdev.notedelight.shared.database.Note
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

class NoteUseCase(
        private val safeRepo: SafeRepo
) {
    val titleChannel: Channel<String> by lazy { return@lazy Channel<String>() }

    fun doOnRelaunchFlow(function: () -> Unit) {
        safeRepo.relaunchFlowEmitter = function
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getNotes(): Flow<List<Note>> = safeRepo.noteDao.getNotes().distinctUntilChanged()

    suspend fun createNote(title: String = "", text: String = ""): Long {
        val date = Date()
        val note = Note(0, title, text, date, date)
        return safeRepo.noteDao.insertNote(note)
    }

    suspend fun saveNote(id: Long, title: String, text: String): Int {
        val note = safeRepo.noteDao.getNoteById(id).copy(
                title = title,
                text = text,
                dateModified = Date()
        )
        return safeRepo.noteDao.updateNote(note)
    }

    suspend fun updateTitle(id: Long, title: String): Int {
        val note = safeRepo.noteDao.getNoteById(id).copy(
                title = title,
                dateModified = Date()
        )
        return safeRepo.noteDao.updateNote(note)
    }

    suspend fun loadNote(noteId: Long): Note = safeRepo.noteDao.getNoteById(noteId)

    suspend fun deleteNote(id: Long): Int = safeRepo.noteDao.deleteNoteById(id)

    suspend fun isChanged(id: Long, title: String, text: String): Boolean {
        val note = safeRepo.noteDao.getNoteById(id)
        return note.title != title || note.text != text
    }

    suspend fun isEmpty(id: Long): Boolean {
        val note = safeRepo.noteDao.getNoteById(id)
        return note.title.isEmpty() && note.text.isEmpty()
    }

}