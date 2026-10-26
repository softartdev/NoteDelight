package com.softartdev.notedelight.interactor

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.popoverPresentationController

class IosShareInteractor : ShareInteractor() {

    override fun shareText(text: String) {
        val activityController = UIActivityViewController(
            activityItems = listOf(text),
            applicationActivities = null
        )
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return
        activityController.popoverPresentationController?.sourceView = rootViewController.view
        rootViewController.presentViewController(activityController, animated = true, completion = null)
    }
}
