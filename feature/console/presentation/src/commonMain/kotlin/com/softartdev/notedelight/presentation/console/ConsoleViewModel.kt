package com.softartdev.notedelight.presentation.console

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.softartdev.notedelight.usecase.console.ConsoleTranscriptEntry
import com.softartdev.notedelight.usecase.console.ConsoleTranscriptEntryKind
import com.softartdev.notedelight.usecase.console.ConsoleUseCase
import com.softartdev.notedelight.usecase.console.ConsoleUseCaseResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConsoleViewModel(
    private val consoleUseCase: ConsoleUseCase,
) : ViewModel() {
    private val logger = Logger.withTag("ConsoleViewModel")

    val stateFlow: StateFlow<ConsoleResult>
        field = MutableStateFlow(ConsoleResult())

    fun onAction(action: ConsoleAction) = when (action) {
        is ConsoleAction.UpdateInput -> updateInput(action.text)
        is ConsoleAction.Submit -> submit()
    }

    private fun updateInput(text: String) {
        stateFlow.update { it.copy(input = text) }
    }

    private fun submit() {
        if (stateFlow.value.running) return
        val input = stateFlow.value.input
        if (input.isBlank()) return
        stateFlow.update { it.copy(running = true) }
        viewModelScope.launch {
            when (val result = consoleUseCase(input)) {
                is ConsoleUseCaseResult.Executed -> {
                    val commandEntry = ConsoleTranscriptEntry(
                        kind = ConsoleTranscriptEntryKind.COMMAND,
                        text = result.normalizedCommand,
                    )
                    stateFlow.update { state ->
                        state.copy(
                            input = "",
                            running = false,
                            transcript = state.transcript + commandEntry + result.entries,
                            commandHistory = state.commandHistory + result.normalizedCommand,
                        )
                    }
                }
                is ConsoleUseCaseResult.ValidationError -> {
                    logger.d { "Validation error: ${result.message}" }
                    stateFlow.update { it.copy(running = false) }
                }
            }
        }
    }
}
