package com.softartdev.notedelight.shared.presentation.note

import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.base.BaseViewModel
import io.github.aakira.napier.Napier


class NoteViewModel(
    private val noteUseCase: NoteUseCase,
) : BaseViewModel<NoteResult>() {

    private var noteId: Long = 0
        get() = when (field) {
            0L -> throw IllegalStateException("Note doesn't loaded")
            else -> field
        }

    override val loadingResult: NoteResult = NoteResult.Loading

    fun createNote() = launch {
        val note = noteUseCase.createNote()
        noteId = note
        Napier.d("Created note with id=$noteId")
        NoteResult.Created(note)
    }

    fun loadNote(id: Long) = launch {
        val note = noteUseCase.loadNote(id)
        noteId = note.id
        Napier.d("Loaded note with id=$noteId")
        NoteResult.Loaded(note)
    }

    fun saveNote(title: String?, text: String) = launch {
        if (title.isNullOrEmpty() && text.isEmpty()) {
            NoteResult.Empty
        } else {
            val noteTitle = createTitleIfNeed(title, text)
            noteUseCase.saveNote(noteId, noteTitle, text)
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
        val changed = noteUseCase.isChanged(noteId, noteTitle, text)
        val empty = noteUseCase.isEmpty(noteId)
        when {
            changed -> NoteResult.CheckSaveChange
            empty -> deleteNoteForResult()
            else -> NoteResult.NavBack
        }
    }

    fun saveNoteAndNavBack(title: String?, text: String) = launch {
        val noteTitle = createTitleIfNeed(title, text)
        noteUseCase.saveNote(noteId, noteTitle, text)
        Napier.d("Saved and nav back")
        NoteResult.NavBack
    }

    fun doNotSaveAndNavBack() = launch {
        val noteIsEmpty = noteUseCase.isEmpty(noteId)
        if (noteIsEmpty) {
            deleteNoteForResult()
        } else {
            Napier.d("Don't save and nav back")
            NoteResult.NavBack
        }
    }

    private suspend fun deleteNoteForResult(): NoteResult {
        noteUseCase.deleteNote(noteId)
        Napier.d("Deleted note with id=$noteId")
        return NoteResult.Deleted
    }

    private suspend fun subscribeToEditTitle() = launch(useIdling = false) {
        val title = noteUseCase.titleChannel.receive()
        NoteResult.TitleUpdated(title)
    }

    override fun errorResult(throwable: Throwable): NoteResult = NoteResult.Error(throwable.message)

    // @androidx.annotation.VisibleForTesting
    fun setIdForTest(id: Long) {
        noteId = id
    }

    private fun createTitleIfNeed(title: String?, text: String) =
        if (title.isNullOrEmpty()) createTitle(text) else title

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
}
