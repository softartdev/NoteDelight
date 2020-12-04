package com.softartdev.notedelight.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.softartdev.notedelight.R

class EmptyView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val messageTextView: TextView
    val addNoteTextView: TextView

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        LayoutInflater.from(context).inflate(R.layout.view_empty, this)

        messageTextView = findViewById(R.id.text_message)
        addNoteTextView = findViewById(R.id.add_note_text_view)
    }

}