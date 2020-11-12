package com.softartdev.notedelight.ui.base

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceFragmentCompat
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ViewModelOwnerDefinition
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.androidx.viewmodel.scope.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.KoinScopeComponent
import org.koin.core.scope.Scope

abstract class BasePrefFragment : PreferenceFragmentCompat(), KoinScopeComponent {

    override val scope: Scope by lazy { fragmentScope() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getKoin().logger.debug("Open Fragment Scope: $scope")
    }

    inline fun <reified T : ViewModel> viewModel(
        qualifier: Qualifier? = null,
        noinline state: BundleDefinition? = null,
        noinline owner: ViewModelOwnerDefinition = { ViewModelOwner.from(this, this) },
        noinline parameters: ParametersDefinition? = null
    ): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
        scope.getViewModel(qualifier, state, owner, T::class, parameters)
    }
}