package com.softartdev.notedelight.shared.presentation.title

import com.softartdev.notedelight.shared.base.BaseViewModel
import com.softartdev.notedelight.shared.db.NoteDAO
import com.softartdev.notedelight.shared.usecase.note.UpdateTitleUseCase

class EditTitleViewModel(
    private val noteDAO: NoteDAO,
    private val updateTitleUseCase: UpdateTitleUseCase
) : BaseViewModel<EditTitleResult>() {

    override val loadingResult: EditTitleResult = EditTitleResult.Loading

    fun loadTitle(noteId: Long) = launch {
        val note = noteDAO.load(noteId)
        EditTitleResult.Loaded(note.title)
    }

    fun editTitle(id: Long, newTitle: String) = launch {
        val (noteId, noteTitle) = id to newTitle.trim()
        when {
            noteTitle.isEmpty() -> EditTitleResult.EmptyTitleError
            else -> {
                updateTitleUseCase(noteId, noteTitle)
                UpdateTitleUseCase.titleChannel.send(noteTitle)
                EditTitleResult.Success
            }
        }
    }

    override fun errorResult(throwable: Throwable) = EditTitleResult.Error(throwable.message)
}
