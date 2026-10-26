@file:OptIn(ExperimentalWasmJsInterop::class)

package com.softartdev.notedelight.interactor

class WebShareInteractor(
    private val snackbarInteractor: SnackbarInteractor,
) : ShareInteractor() {

    override fun shareText(text: String) {
        if (canWebShare()) {
            webShare(text)
        } else {
            snackbarInteractor.showMessage(SnackbarMessage.Copyable(text))
        }
    }
}

@JsFun("() => typeof navigator !== 'undefined' && typeof navigator.share === 'function'")
private external fun canWebShare(): Boolean

@JsFun("(text) => { navigator.share({ text: text }).catch(() => {}); }")
private external fun webShare(text: String)
