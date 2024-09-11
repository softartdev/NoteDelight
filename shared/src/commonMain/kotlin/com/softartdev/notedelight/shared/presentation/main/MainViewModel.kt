package com.softartdev.notedelight.shared.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softartdev.notedelight.shared.db.SafeRepo
import com.softartdev.notedelight.shared.navigation.AppNavGraph
import com.softartdev.notedelight.shared.navigation.Router
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MainViewModel(
    private val safeRepo: SafeRepo,
    private val router: Router
) : ViewModel() {
    private val mutableStateFlow: MutableStateFlow<NoteListResult> = MutableStateFlow(
        value = NoteListResult.Loading
    )
    val stateFlow: StateFlow<NoteListResult> = mutableStateFlow

    init {
        safeRepo.relaunchListFlowCallback = this::updateNotes
    }

    fun updateNotes() = viewModelScope.launch(Dispatchers.Main) {
        safeRepo.noteDAO.listFlow
            .onStart { mutableStateFlow.value = NoteListResult.Loading }
            .map(transform = NoteListResult::Success)
            .onEach(action = mutableStateFlow::emit)
            .flowOn(Dispatchers.IO)
            .catch { throwable ->
                Napier.e("❌", throwable)
                mutableStateFlow.value = NoteListResult.Error(throwable.message)
                if (throwable::class.simpleName.orEmpty().contains("SQLite")) {
                    router.navigateClearingBackStack(AppNavGraph.SignIn.name)
                }
            }.launchIn(this)
    }.ensureActive()

    fun onNoteClicked(id: Long) = router.navigate(route = "${AppNavGraph.Details.name}/$id")

    fun onSettingsClicked() = router.navigate(route = AppNavGraph.Settings.name)
}