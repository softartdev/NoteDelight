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
import kotlinx.coroutines.Job
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ViewModelOwnerDefinition
import org.koin.androidx.viewmodel.scope.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

abstract class BaseDialogFragment(
        @StringRes private val titleStringRes: Int,
        @LayoutRes private val dialogLayoutRes: Int
) : AppCompatDialogFragment(), AndroidScopeComponent, DialogInterface.OnShowListener {

    override val scope: Scope by fragmentScope()

    internal var lifecycleStateFlowJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scope.logger.debug("Open Fragment Scope: $scope")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(titleStringRes)
            .setView(dialogLayoutRes)
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            .also { it.setOnShowListener(this@BaseDialogFragment) }

    override fun onShow(dialog: DialogInterface?) {
        val okButton = requireDialog().findViewById<Button>(android.R.id.button1)
        okButton.setOnClickListener { onOkClicked() }
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

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        cancelLifecycleJobIfNeed()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_FIRST_USER, null)
        cancelLifecycleJobIfNeed()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelLifecycleJobIfNeed()
    }

    private fun cancelLifecycleJobIfNeed() {
        lifecycleStateFlowJob?.cancel()
        lifecycleStateFlowJob = null
    }

    inline fun <reified T : ViewModel> viewModel(
        qualifier: Qualifier? = null,
        noinline owner: ViewModelOwnerDefinition = { ViewModelOwner.from(this, this) },
        noinline parameters: ParametersDefinition? = null
    ): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
        scope.getViewModel(qualifier, owner, T::class, parameters = parameters)
    }

    companion object {
        internal const val DIALOG_REQUEST_CODE = 378
    }
}