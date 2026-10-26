package com.softartdev.notedelight

import android.content.ClipDescription
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.softartdev.notedelight.interactor.ShareInteractor
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val shareInteractor: ShareInteractor by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
        if (savedInstanceState == null) {
            handleIncomingIntent(intent)
        }
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent?.action != Intent.ACTION_SEND) return
        if (intent.type?.startsWith(ClipDescription.MIMETYPE_TEXT_PLAIN) != true) return
        val sharedText: String? = intent.getStringExtra(Intent.EXTRA_TEXT)
            ?: intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
        intent.removeExtra(Intent.EXTRA_TEXT)
        if (sharedText.isNullOrBlank()) return
        shareInteractor.sharedTextStateFlow.value = sharedText
    }
}
