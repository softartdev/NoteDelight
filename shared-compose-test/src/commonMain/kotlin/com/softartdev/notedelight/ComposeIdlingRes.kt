package com.softartdev.notedelight

import androidx.compose.ui.test.IdlingResource
import com.softartdev.notedelight.shared.base.IdlingRes

object ComposeIdlingRes : IdlingResource {

    override val isIdleNow: Boolean
        get() = IdlingRes.isIdleNow
}
