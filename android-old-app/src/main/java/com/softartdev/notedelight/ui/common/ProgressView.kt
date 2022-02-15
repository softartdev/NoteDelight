package com.softartdev.notedelight.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.softartdev.notedelight.R

class ProgressView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val progress_bar: ProgressBar
    val loadingTextView: TextView
    val progressTextView: TextView

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        LayoutInflater.from(context).inflate(R.layout.view_progress, this)

        progress_bar = findViewById(R.id.progress_bar)
        loadingTextView = findViewById(R.id.text_loading)
        progressTextView = findViewById(R.id.text_progress)
    }
}
