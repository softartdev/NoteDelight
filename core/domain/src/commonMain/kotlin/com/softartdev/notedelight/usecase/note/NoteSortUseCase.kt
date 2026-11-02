package com.softartdev.notedelight.usecase.note

import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.model.NoteSort
import com.softartdev.notedelight.repository.PreferencesRepo

class NoteSortUseCase(
    private val preferencesRepo: PreferencesRepo,
) : (NoteSort) -> Unit {

    init {
        NoteDAO.noteSortFlow.value = NoteSort(
            field = preferencesRepo.sortField,
            order = preferencesRepo.sortOrder,
        )
    }

    override fun invoke(noteSort: NoteSort) {
        preferencesRepo.sortField = noteSort.field
        preferencesRepo.sortOrder = noteSort.order
        NoteDAO.noteSortFlow.value = noteSort
    }
}
