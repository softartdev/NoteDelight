package com.softartdev.notedelight.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.softartdev.notedelight.R

class ErrorView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val titleTextView: TextView
    val messageTextView: TextView
    val reloadButton: Button
    val cancelButton: Button

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        LayoutInflater.from(context).inflate(R.layout.view_error, this)

        titleTextView = findViewById(R.id.text_error_title)
        messageTextView = findViewById(R.id.text_error_message)
        reloadButton = findViewById(R.id.button_reload)
        cancelButton = findViewById(R.id.button_cancel)

        cancelButton.setOnClickListener { this@ErrorView.visibility = View.GONE }
    }
}
