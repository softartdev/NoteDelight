package com.softartdev.notedelight.interactor

import android.content.ClipDescription
import android.content.Context
import android.content.Intent

class AndroidShareInteractor(private val context: Context) : ShareInteractor() {

    override fun shareText(text: String) {
        val sendIntent = Intent(Intent.ACTION_SEND)
            .setType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            .putExtra(Intent.EXTRA_TEXT, text)
        val chooserIntent = Intent.createChooser(sendIntent, null)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }
}
