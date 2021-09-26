package com.softartdev.notedelight.shared.presentation.main

import com.softartdev.notedelight.shared.base.BaseViewModel
import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.db.Note
import kotlinx.coroutines.flow.map


class MainViewModel(
    private val noteUseCase: NoteUseCase,
) : BaseViewModel<NoteListResult>() {

    override val loadingResult: NoteListResult = NoteListResult.Loading

    init {
        noteUseCase.doOnRelaunchFlow(this::updateNotes)
    }

    fun updateNotes() = launch(
        flow = noteUseCase.getNotes().map { notes: List<Note> ->
            NoteListResult.Success(notes)
        })

    override fun errorResult(
        throwable: Throwable,
    ): NoteListResult = when (throwable::class.simpleName?.contains("SQLite")) {
        true -> NoteListResult.NavMain
        else -> NoteListResult.Error(throwable.message)
    }

    override fun onCleared() = noteUseCase.doOnRelaunchFlow(null)
}