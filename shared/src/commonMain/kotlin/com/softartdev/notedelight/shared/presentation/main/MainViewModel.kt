package com.softartdev.notedelight.shared.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.db.SafeRepo
import com.softartdev.notedelight.shared.navigation.AppNavGraph
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.util.CoroutineDispatchers
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
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

    private val pagingDataFlow: Flow<PagingData<Note>>
        get() = Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = safeRepo.noteDAO::pagingSource
        ).flow.cachedIn(viewModelScope)

    init {
        safeRepo.relaunchListFlowCallback = this::updateNotes
    }

    fun updateNotes() = viewModelScope.launch(coroutineDispatchers.main) {
        if (job?.isActive == true) {
            job?.cancel()
        }
        job = flowOf(pagingDataFlow)
            .onStart { mutableStateFlow.value = NoteListResult.Loading }
            .map(transform = NoteListResult::Success)
            .onEach(action = mutableStateFlow::emit)
            .flowOn(coroutineDispatchers.io)
            .catch { throwable ->
                Napier.e("❌", throwable)
                mutableStateFlow.value = NoteListResult.Error(throwable.message)
                if (throwable::class.simpleName.orEmpty().contains("SQLite")) {
                    router.navigateClearingBackStack(AppNavGraph.SignIn)
                }
            }.launchIn(this)
    }

    fun onNoteClicked(id: Long) = router.navigate(route = AppNavGraph.Details(noteId = id))

    fun onSettingsClicked() = router.navigate(route = AppNavGraph.Settings)
}