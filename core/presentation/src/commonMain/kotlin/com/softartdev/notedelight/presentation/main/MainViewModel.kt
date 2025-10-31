package com.softartdev.notedelight.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.softartdev.notedelight.interactor.AdaptiveInteractor
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.repository.SafeRepo
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
    private val adaptiveInteractor: AdaptiveInteractor,
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

    fun onAction(action: MainAction) = when (action) {
        is MainAction.OnNoteClick -> viewModelScope.launch {
            adaptiveInteractor.selectedNoteIdStateFlow.value = action.id
            router.adaptiveNavigateToDetail(contentKey = action.id)
        }
        is MainAction.OnSettingsClick -> router.navigate(route = AppNavGraph.Settings)
        is MainAction.OnRefresh -> updateNotes()
    }

    fun launchNotes() {
        checkDbConnection()
        if (job != null) return
        updateNotes()
    }

    private fun checkDbConnection() = viewModelScope.launch(coroutineDispatchers.io) {
        try {
            val count: Long = safeRepo.noteDAO.count()
            Napier.d("check DB connection, notes: $count")
        } catch (throwable: Throwable) {
            handleError(throwable)
        }
    }

    fun updateNotes() {
        job?.cancel()
        try {
            mutableStateFlow.value = NoteListResult.Loading
            val pagingDataFlow: Flow<PagingData<Note>> = safeRepo.noteDAO.pagingDataFlow
                .cachedIn(viewModelScope)
            mutableStateFlow.value = NoteListResult.Success(result = pagingDataFlow, selectedId = null)
        } catch (throwable: Throwable) {
            handleError(throwable)
        }
        job = viewModelScope.launch {
            adaptiveInteractor.selectedNoteIdStateFlow.collect { selectedId: Long? ->
                val currentState = mutableStateFlow.value
                if (currentState is NoteListResult.Success) {
                    mutableStateFlow.value = currentState.copy(selectedId = selectedId)
                }
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
}