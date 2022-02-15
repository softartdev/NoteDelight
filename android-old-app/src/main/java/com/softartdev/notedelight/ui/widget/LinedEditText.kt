package com.softartdev.notedelight.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet

import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.softartdev.notedelight.R

/**
 * Defines a custom EditText View that draws lines between each line of text that is displayed.
 */
class LinedEditText @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    // Creates a Rect and a Paint object, and sets the style and color of the Paint object.
    private val rect = Rect()
    private val paint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.secondary)
    }

    /**
     * This is called to draw the LinedEditText object
     * @param canvas The canvas on which the background is drawn.
     */
    override fun onDraw(canvas: Canvas) {
        if (lineCount > 1) {
            /*
            * Draws one line in the rectangle for every line of text in the EditText
            */
            for (i in 0 until lineCount - 1) {
                // Gets the baseline coordinates for the current line of text
                val baseline = getLineBounds(i, rect)
                /*
                 * Draws a line in the background from the left of the rectangle to the right,
                 * at a vertical position one dip below the baseline, using the "paint" object
                 * for details.
                 */
                val yFloat = (baseline + 1).toFloat()
                canvas.drawLine(rect.left.toFloat(), yFloat, rect.right.toFloat(), yFloat, paint)
            }
        }
        // Finishes up by calling the parent method
        super.onDraw(canvas)
    }
}
