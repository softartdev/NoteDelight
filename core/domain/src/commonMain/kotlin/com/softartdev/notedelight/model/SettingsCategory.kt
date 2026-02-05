package com.softartdev.notedelight.model

enum class SettingsCategory {
    Theme,
    Security,
    Backup,
    Info;

    val id: Long = ordinal.toLong()

    companion object {
        fun fromId(id: Long?): SettingsCategory? = id?.toInt()?.let(entries::get)
    }
}
