package com.softartdev.notedelight.db

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper

/**
 * Lets Room own schema creation while SafeRoom remains responsible for opening SQLCipher.
 * SafeRoom eagerly assigns [version] after [onCreate], so a newly created database is reset to
 * version zero before Room inspects it. Existing v2 databases are left untouched.
 */
class Room3SupportCallback : SupportSQLiteOpenHelper.Callback(version = 2) {
    private var wasCreated = false

    override fun onCreate(db: SupportSQLiteDatabase) {
        wasCreated = true
    }

    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    override fun onOpen(db: SupportSQLiteDatabase) {
        if (wasCreated) {
            db.version = 0
            wasCreated = false
        }
    }
}