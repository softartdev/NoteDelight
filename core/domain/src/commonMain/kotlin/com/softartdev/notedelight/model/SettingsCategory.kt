package com.softartdev.notedelight.model

enum class SettingsCategory {
    Appearance,
    Security,
    Backup,
    Console,
    Info;

    val id: Long = ordinal.toLong()

    companion object {
        fun fromId(id: Long?): SettingsCategory? = id?.toInt()?.let(entries::get)
    }
}
