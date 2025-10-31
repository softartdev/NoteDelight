package com.softartdev.notedelight.interactor

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import notedelight.ui.shared.generated.resources.Res
import notedelight.ui.shared.generated.resources.copy
import notedelight.ui.shared.generated.resources.note_deleted
import notedelight.ui.shared.generated.resources.note_empty
import notedelight.ui.shared.generated.resources.note_saved
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

class SnackbarInteractorImpl : SnackbarInteractor {
    private var snackbarHostState: SnackbarHostState? = null
    private var clipboardManager: ClipboardManager? = null
    private var coroutineScope: CoroutineScope? = null

    override fun setDependencies(hostState: Any, clipboard: Any, coroutineScope: CoroutineScope) {
        this.snackbarHostState = hostState as SnackbarHostState
        this.clipboardManager = clipboard as ClipboardManager
        this.coroutineScope = coroutineScope
    }

    override fun releaseDependencies() {
        this.snackbarHostState = null
        this.clipboardManager = null
        this.coroutineScope = null
    }

    override fun showMessage(message: SnackbarMessage): Job? = coroutineScope?.launch {
        val hostState: SnackbarHostState = snackbarHostState ?: return@launch
        when (message) {
            is SnackbarMessage.Simple -> hostState.showSnackbar(message = message.text)
            is SnackbarMessage.Copyable -> {
                val result: SnackbarResult = hostState.showSnackbar(
                    message = message.text,
                    actionLabel = getString(Res.string.copy),
                    duration = SnackbarDuration.Long
                )
                if (result == SnackbarResult.ActionPerformed) {
                    clipboardManager?.setText(AnnotatedString(message.text))
                }
            }
            is SnackbarMessage.Resource -> {
                val resource: StringResource = when (message.res) {
                    SnackbarTextResource.SAVED -> Res.string.note_saved
                    SnackbarTextResource.EMPTY -> Res.string.note_empty
                    SnackbarTextResource.DELETED -> Res.string.note_deleted
                }
                var text: String = getString(resource = resource)
                if (message.suffix.isNotEmpty()) {
                    text += ": ${message.suffix}"
                }
                hostState.showSnackbar(message = text)
            }
        }
    }
}