package com.softartdev.notedelight.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.util.CoroutineDispatchers
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val safeRepo: SafeRepo,
    private val router: Router,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    private val mutableStateFlow: MutableStateFlow<NoteListResult> = MutableStateFlow(
        value = NoteListResult.Loading
    )
    val stateFlow: StateFlow<NoteListResult> = mutableStateFlow

    private var job: Job? = null

    init {
        safeRepo.relaunchListFlowCallback = this::updateNotes
    }

    fun launchNotes() {
        checkDbConnection()
        if (job != null) return
        updateNotes()
    }

    private fun checkDbConnection() = viewModelScope.launch {
        try {
            val count: Long = safeRepo.noteDAO.count
            Napier.d("check DB connection, notes: $count")
        } catch (throwable: Throwable) {
            handleError(throwable)
        }
    }

    fun updateNotes() {
        job?.cancel()
        job = viewModelScope.launch(coroutineDispatchers.io) {
            try {
                mutableStateFlow.value = NoteListResult.Loading
                val pagingDataFlow: Flow<PagingData<Note>> = safeRepo.noteDAO.pagingDataFlow
                    .cachedIn(viewModelScope)
                mutableStateFlow.value = NoteListResult.Success(result = pagingDataFlow)
            } catch (throwable: Throwable) {
                handleError(throwable)
            }
        }
    }

    private fun handleError(throwable: Throwable) {
        Napier.e("❌", throwable)
        if (isDbError(throwable)) {
            router.navigateClearingBackStack(AppNavGraph.Splash)
        }
        mutableStateFlow.value = NoteListResult.Error(throwable.message)
    }

    private fun isDbError(throwable: Throwable): Boolean {
        if (throwable::class.simpleName.orEmpty().contains("SQLite", ignoreCase = true)) return true
        return throwable.message.orEmpty().contains("database not open", ignoreCase = true)
    }

    fun onNoteClicked(id: Long) = router.navigate(route = AppNavGraph.Details(noteId = id))

    fun onSettingsClicked() = router.navigate(route = AppNavGraph.Settings)
}