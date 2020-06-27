package com.softartdev.notedelight.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat
import androidx.preference.Preference

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
}

/**
 * Extension method to provide show keyboard for View.
 */
fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, 0)
}

/**
 * Extension method to provide show keyboard for View.
 */
fun View.gone() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}

/**
 * Extension method to provide show keyboard for View.
 */
fun View.visible() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

fun View.invisible() {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
}

fun MenuItem.tintIcon(
        context: Context,
        @ColorInt color: Int = getThemeColor(context, android.R.attr.textColorPrimary)
) {
    val drawableWrap = DrawableCompat.wrap(icon).mutate()
    DrawableCompat.setTint(drawableWrap, color)
    icon = drawableWrap
}

fun Preference.tintIcon() {
    val drawableWrap = DrawableCompat.wrap(icon).mutate()
    val color = getThemeColor(context, android.R.attr.textColorPrimary)
    DrawableCompat.setTint(drawableWrap, color)
    icon = drawableWrap
}

/**
 * Queries the theme of the given `context` for a theme color.
 *
 * @param context   the context holding the current theme.
 * @param attrResId the theme color attribute to resolve.
 * @return the theme color
 */
@ColorInt
fun getThemeColor(context: Context, @AttrRes attrResId: Int): Int {
    val a = context.obtainStyledAttributes(null, intArrayOf(attrResId))
    try {
        return a.getColor(0, Color.MAGENTA)
    } finally {
        a.recycle()
    }
}

fun createTitle(text: String): String {
    // Get the note's length
    val length = text.length

    // Sets the title by getting a substring of the text that is 31 characters long
    // or the number of characters in the note plus one, whichever is smaller.
    var title = text.substring(0, 30.coerceAtMost(length))

    // If the resulting length is more than 30 characters, chops off any
    // trailing spaces
    if (length > 30) {
        val lastSpace: Int = title.lastIndexOf(' ')
        if (lastSpace > 0) {
            title = title.substring(0, lastSpace)
        }
    }
    return title
}
