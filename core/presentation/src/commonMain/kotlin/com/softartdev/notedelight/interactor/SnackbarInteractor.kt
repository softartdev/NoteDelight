package com.softartdev.notedelight.interactor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

interface SnackbarInteractor {
    fun setDependencies(hostState: Any, clipboard: Any, coroutineScope: CoroutineScope)
    fun releaseDependencies()
    fun showMessage(message: SnackbarMessage): Job?
}

sealed interface SnackbarMessage {
    data class Simple(val text: String) : SnackbarMessage
    data class Copyable(val text: String) : SnackbarMessage
    data class Resource(val res: SnackbarTextResource, val suffix: String = "") : SnackbarMessage
}

enum class SnackbarTextResource {
    SAVED,
    EMPTY,
    DELETED
}
