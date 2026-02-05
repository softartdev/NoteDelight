package com.softartdev.notedelight.ui.settings.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIViewController
import platform.darwin.NSObject

@Composable
actual fun rememberPlatformDatabaseFilePicker(): DatabaseFilePicker = remember {
    return@remember IosDatabaseFilePicker()
}

class IosDatabaseFilePicker : DatabaseFilePicker {

    /**
     * Launches when the user clicks the export button on the settings screen.
     */
    override fun launchExport(defaultFileName: String, onPicked: (String?) -> Unit) {
        val delegate = PickerDelegate { url: NSURL? ->
            val directoryPath = url?.path
            if (directoryPath.isNullOrEmpty()) {
                onPicked(null)
                return@PickerDelegate
            }
            val separator = if (directoryPath.endsWith("/")) "" else "/"
            onPicked("$directoryPath$separator$defaultFileName")
        }
        val picker = UIDocumentPickerViewController(
            documentTypes = listOf("public.folder"),
            inMode = UIDocumentPickerMode.UIDocumentPickerModeOpen
        )
        picker.delegate = delegate
        picker.allowsMultipleSelection = false
        presentPicker(picker)
    }

    /**
     * Launches when the user clicks the import button on the settings screen.
     */
    override fun launchImport(onPicked: (String?) -> Unit) {
        val delegate = PickerDelegate { url: NSURL? ->
            onPicked(url?.path)
        }
        val picker = UIDocumentPickerViewController(
            documentTypes = listOf("public.data"),
            inMode = UIDocumentPickerMode.UIDocumentPickerModeImport
        )
        picker.delegate = delegate
        picker.allowsMultipleSelection = false
        presentPicker(picker)
    }
}

private class PickerDelegate(
    private val onPicked: (NSURL?) -> Unit,
) : NSObject(), UIDocumentPickerDelegateProtocol {

    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
        onPicked(url)
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        onPicked(null)
    }
}

private fun presentPicker(picker: UIDocumentPickerViewController) {
    val controller: UIViewController = topViewController() ?: return
    controller.presentViewController(picker, animated = true, completion = null)
}

private fun topViewController(): UIViewController? {
    var controller = UIApplication.sharedApplication.keyWindow?.rootViewController
    while (controller?.presentedViewController != null) {
        controller = controller.presentedViewController
    }
    return controller
}
