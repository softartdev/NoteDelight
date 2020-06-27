package com.softartdev.notedelight.ui.main

import com.softartdev.notedelight.shared.data.NoteUseCase
import com.softartdev.notedelight.shared.database.Note
import com.softartdev.notedelight.ui.base.BaseViewModel
import kotlinx.coroutines.flow.map
import net.sqlcipher.database.SQLiteException


class MainViewModel (
        private val noteUseCase: NoteUseCase
) : BaseViewModel<NoteListResult>() {

    override val loadingResult: NoteListResult = NoteListResult.Loading

    init {
        noteUseCase.doOnRelaunchFlow(this::updateNotes)
    }

    fun updateNotes() = launch(
            flow = noteUseCase.getNotes().map { notes: List<Note> ->
                NoteListResult.Success(notes)
            })

    override fun errorResult(throwable: Throwable): NoteListResult = when (throwable) {
        is SQLiteException -> NoteListResult.NavMain
        else -> NoteListResult.Error(throwable.message)
    }
}