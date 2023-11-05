package com.softartdev.notedelight.shared.presentation.note

import com.softartdev.notedelight.shared.base.BaseViewModel
import com.softartdev.notedelight.shared.db.NoteDAO
import com.softartdev.notedelight.shared.usecase.note.CreateNoteUseCase
import com.softartdev.notedelight.shared.usecase.note.SaveNoteUseCase
import com.softartdev.notedelight.shared.usecase.note.UpdateTitleUseCase
import io.github.aakira.napier.Napier

class NoteViewModel(
    private val noteDAO: NoteDAO,
    private val createNoteUseCase: CreateNoteUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
) : BaseViewModel<NoteResult>() {

    private var noteId: Long = 0
        get() = when (field) {
            0L -> throw IllegalStateException("Note doesn't loaded")
            else -> field
        }

    override val loadingResult: NoteResult = NoteResult.Loading

    fun createNote() = launch {
        noteId = createNoteUseCase()
        Napier.d("Created note with id=$noteId")
        NoteResult.Created(noteId)
    }

    fun loadNote(id: Long) = launch {
        val note = noteDAO.load(id)
        noteId = note.id
        Napier.d("Loaded note with id=$noteId")
        NoteResult.Loaded(note)
    }

    fun saveNote(title: String?, text: String) = launch {
        if (title.isNullOrEmpty() && text.isEmpty()) {
            NoteResult.Empty
        } else {
            val noteTitle = createTitleIfNeed(title, text)
            saveNoteUseCase(noteId, noteTitle, text)
            Napier.d("Saved note with id=$noteId")
            NoteResult.Saved(noteTitle)
        }
    }

    fun editTitle() = launch {
        subscribeToEditTitle()
        NoteResult.NavEditTitle(noteId)
    }

    fun deleteNote() = launch { deleteNoteForResult() }

    fun checkSaveChange(title: String?, text: String) = launch {
        val noteTitle = createTitleIfNeed(title, text)
        val changed = isChanged(noteId, noteTitle, text)
        val empty = isEmpty(noteId)
        when {
            changed -> NoteResult.CheckSaveChange
            empty -> deleteNoteForResult()
            else -> NoteResult.NavBack
        }
    }

    fun saveNoteAndNavBack(title: String?, text: String) = launch {
        val noteTitle = createTitleIfNeed(title, text)
        saveNoteUseCase(noteId, noteTitle, text)
        Napier.d("Saved and nav back")
        NoteResult.NavBack
    }

    fun doNotSaveAndNavBack() = launch {
        val noteIsEmpty = isEmpty(noteId)
        if (noteIsEmpty) {
            deleteNoteForResult()
        } else {
            Napier.d("Don't save and nav back")
            NoteResult.NavBack
        }
    }

    private fun deleteNoteForResult(): NoteResult {
        noteDAO.delete(noteId)
        Napier.d("Deleted note with id=$noteId")
        return NoteResult.Deleted
    }

    private suspend fun subscribeToEditTitle() = launch(useIdling = false) {
        val title = UpdateTitleUseCase.titleChannel.receive()
        NoteResult.TitleUpdated(title)
    }

    override fun errorResult(throwable: Throwable): NoteResult = NoteResult.Error(throwable.message)

    override fun onCleared() {
        super.onCleared()
        resetLoadingResult() // workaround due to koin uses remember function of compose
    }

    // @androidx.annotation.VisibleForTesting
    fun setIdForTest(id: Long) {
        noteId = id
    }

    private fun createTitleIfNeed(title: String?, text: String) =
        if (title.isNullOrEmpty()) createTitle(text) else title

    //TODO trim '\n'
    private fun createTitle(text: String): String {
        // Get the note's length
        val length = text.length

        // Sets the title by getting a substring of the text that is 31 characters long
        // or the number of characters in the note plus one, whichever is smaller.
        var title = text.substring(0, 30.coerceAtMost(length))

        // If the resulting length is more than 30 characters, chops off any
        // trailing spaces
        if (length > 30) {
            val lastSpace: Int = title.lastIndexOf(' ')
            if (lastSpace > 0) {
                title = title.substring(0, lastSpace)
            }
        }
        return title
    }

    private fun isChanged(id: Long, title: String, text: String): Boolean {
        val note = noteDAO.load(id)
        return note.title != title || note.text != text
    }

    private fun isEmpty(id: Long): Boolean {
        val note = noteDAO.load(id)
        return note.title.isEmpty() && note.text.isEmpty()
    }
}
