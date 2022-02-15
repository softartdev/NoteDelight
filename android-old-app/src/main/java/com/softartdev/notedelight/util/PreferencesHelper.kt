package com.softartdev.notedelight.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.softartdev.notedelight.R

class PreferencesHelper(context: Context) {

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val themeKey = context.getString(R.string.theme_key)
    private val themeDefaultEntry = context.getString(R.string.default_theme_entry)
    private val hideScreenContentsKey = context.getString(R.string.hide_screen_contents_key)

    var themeEntry: String
        get() = preferences.getString(themeKey, themeDefaultEntry) ?: themeDefaultEntry
        set(value) = preferences.edit().putString(themeKey, value).apply()

    var hideScreenContentsEntry: Boolean
        get() = preferences.getBoolean(hideScreenContentsKey, false)
        set(value) = preferences.edit().putBoolean(hideScreenContentsKey, value).apply()

    fun clear() = preferences.edit().clear().apply()

}
