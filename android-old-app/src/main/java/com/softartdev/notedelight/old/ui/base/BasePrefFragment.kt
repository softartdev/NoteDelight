package com.softartdev.notedelight.old.ui.base

import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.fragmentScope
import org.koin.core.scope.Scope

abstract class BasePrefFragment : PreferenceFragmentCompat(), AndroidScopeComponent {

    override val scope: Scope by fragmentScope()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scope.logger.debug("Open Fragment Scope: $scope")
    }
}