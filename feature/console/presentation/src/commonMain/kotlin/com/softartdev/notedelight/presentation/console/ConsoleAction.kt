package com.softartdev.notedelight.presentation.console

sealed interface ConsoleAction {
    data class UpdateInput(val text: String) : ConsoleAction
    data object Submit : ConsoleAction
}
