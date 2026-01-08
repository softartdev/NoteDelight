package com.softartdev.notedelight.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.interactor.AdaptiveInteractor
import com.softartdev.notedelight.model.Note
import com.softartdev.notedelight.navigation.AppNavGraph
import com.softartdev.notedelight.navigation.Router
import com.softartdev.notedelight.repository.SafeRepo
import com.softartdev.notedelight.util.CoroutineDispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val safeRepo: SafeRepo,
    private val router: Router,
    private val adaptiveInteractor: AdaptiveInteractor,
    private val coroutineDispatchers: CoroutineDispatchers,
) : ViewModel() {
    private val logger = Logger.withTag(this@MainViewModel::class.simpleName.toString())
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

    fun launchNotes() = viewModelScope.launch {
        if (!hasDbConnection()) return@launch
        if (job != null) return@launch
        updateNotes()
    }

    private suspend fun hasDbConnection(): Boolean {
        try {
            val count: Long = withContext(coroutineDispatchers.io) { safeRepo.noteDAO.count() }
            logger.d { "check DB connection, notes: $count" }
            return true
        } catch (throwable: Throwable) {
            handleError("Error checking DB connection", throwable)
            return false
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
            handleError("Error loading notes", throwable)
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

    private fun handleError(message: String, throwable: Throwable) {
        logger.e(message, throwable)
        if (isDbError(throwable)) viewModelScope.launch(coroutineDispatchers.main) {
            router.navigateClearingBackStack(AppNavGraph.Splash)
        }
        mutableStateFlow.value = NoteListResult.Error(throwable.message)
    }

    private fun isDbError(throwable: Throwable): Boolean {
        if (throwable::class.simpleName.orEmpty().contains("SQLite", ignoreCase = true)) return true
        return throwable.message.orEmpty().contains("database not open", ignoreCase = true)
    }
}