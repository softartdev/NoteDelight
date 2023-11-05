package com.softartdev.notedelight.shared.presentation.main

import com.softartdev.notedelight.shared.base.BaseViewModel
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.db.NoteDAO
import com.softartdev.notedelight.shared.db.SafeRepo
import kotlinx.coroutines.flow.map

class MainViewModel(
    private val safeRepo: SafeRepo,
    private val noteDAO: NoteDAO,
) : BaseViewModel<NoteListResult>() {

    override val loadingResult: NoteListResult = NoteListResult.Loading

    init {
        safeRepo.relaunchListFlowCallback = this::updateNotes
    }

    fun updateNotes() = launch(
        flow = noteDAO.listFlow.map { notes: List<Note> ->
            NoteListResult.Success(notes)
        })

    override fun errorResult(throwable: Throwable): NoteListResult {
        val errorName: String = throwable::class.simpleName.orEmpty()
        return when {
            errorName.contains("SQLite") -> NoteListResult.NavSignIn
            else -> NoteListResult.Error(throwable.message)
        }
    }

    override fun onCleared() {
        safeRepo.relaunchListFlowCallback = null
    }
}