package com.softartdev.notedelight.ui.base

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.ViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ViewModelOwnerDefinition
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.androidx.viewmodel.scope.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.KoinScopeComponent
import org.koin.core.scope.Scope

abstract class BaseDialogFragment(
        @StringRes private val titleStringRes: Int,
        @LayoutRes private val dialogLayoutRes: Int
) : AppCompatDialogFragment(), KoinScopeComponent {

    override val scope: Scope by lazy { fragmentScope() }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(titleStringRes)
                    .setView(dialogLayoutRes)
                    .setPositiveButton(android.R.string.ok, null)
                    .setNegativeButton(android.R.string.cancel, null)
                    .create()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireDialog().setOnShowListener {
            val okButton = requireDialog().findViewById<Button>(android.R.id.button1)
            okButton.setOnClickListener { onOkClicked() }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getKoin().logger.debug("Open Fragment Scope: $scope")
    }

    abstract fun onOkClicked()

    fun showError(message: String?) = parentFragment?.view?.let {
        showSnackbar(it, message.orEmpty())
    } ?: showToast(message)

    private fun showSnackbar(parentFragmentView: View, text: CharSequence) = Snackbar
            .make(parentFragmentView, text, Snackbar.LENGTH_LONG)
            .show()

    private fun showToast(text: String?) = Toast
            .makeText(requireContext(), text, Toast.LENGTH_LONG)
            .show()

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_FIRST_USER, null)
    }

    override fun onDestroy() {
        scope.close()
        super.onDestroy()
    }

    inline fun <reified T : ViewModel> viewModel(
        qualifier: Qualifier? = null,
        noinline state: BundleDefinition? = null,
        noinline owner: ViewModelOwnerDefinition = { ViewModelOwner.from(this, this) },
        noinline parameters: ParametersDefinition? = null
    ): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
        scope.getViewModel(qualifier, state, owner, T::class, parameters)
    }

    companion object {
        internal const val DIALOG_REQUEST_CODE = 378
    }
}