package com.softartdev.notedelight.ui.settings.detail

enum class ConsoleTips(val sql: String) {
    CIPHER_VERSION("PRAGMA cipher_version;"),
    SQLITE_VERSION("SELECT sqlite3mc_version();"),
    LAST_INSERT_ROWID("SELECT last_insert_rowid();"),
    SELECT_NOTES("SELECT * FROM note;"),
}
