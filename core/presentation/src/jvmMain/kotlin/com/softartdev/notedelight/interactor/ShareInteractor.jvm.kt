package com.softartdev.notedelight.interactor

class JvmShareInteractor(
    private val snackbarInteractor: SnackbarInteractor,
) : ShareInteractor() {

    override fun shareText(text: String) {
        snackbarInteractor.showMessage(SnackbarMessage.Copyable(text))
    }
}
