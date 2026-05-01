package com.softartdev.notedelight.interactor

import androidx.compose.ui.autofill.AutofillManager

class AutofillInteractorImpl : AutofillInteractor {
    private var autofillManager: AutofillManager? = null

    override fun attach(autofillManager: Any) {
        this.autofillManager = autofillManager as AutofillManager
    }

    override fun commit() {
        autofillManager?.commit()
    }

    override fun cancel() {
        autofillManager?.cancel()
    }

    override fun detach() {
        autofillManager = null
    }
}
