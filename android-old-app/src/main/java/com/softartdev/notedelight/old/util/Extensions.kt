package com.softartdev.notedelight.old.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
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
    val ic: Drawable = icon ?: return
    val drawableWrap = DrawableCompat.wrap(ic).mutate()
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
