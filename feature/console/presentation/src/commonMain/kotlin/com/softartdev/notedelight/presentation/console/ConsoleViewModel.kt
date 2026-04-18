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
    private val mutableStateFlow = MutableStateFlow(ConsoleResult())
    val stateFlow: StateFlow<ConsoleResult> = mutableStateFlow

    fun onAction(action: ConsoleAction) = when (action) {
        is ConsoleAction.UpdateInput -> updateInput(action.text)
        is ConsoleAction.Submit -> submit()
    }

    private fun updateInput(text: String) {
        mutableStateFlow.update { it.copy(input = text) }
    }

    private fun submit() {
        if (mutableStateFlow.value.running) return
        val input = mutableStateFlow.value.input
        if (input.isBlank()) return
        mutableStateFlow.update { it.copy(running = true) }
        viewModelScope.launch {
            when (val result = consoleUseCase(input)) {
                is ConsoleUseCaseResult.Executed -> {
                    val commandEntry = ConsoleTranscriptEntry(
                        kind = ConsoleTranscriptEntryKind.COMMAND,
                        text = result.normalizedCommand,
                    )
                    mutableStateFlow.update { state ->
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
                    mutableStateFlow.update { it.copy(running = false) }
                }
            }
        }
    }
}
