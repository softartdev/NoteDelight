package com.softartdev.notedelight.db

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.room.Room
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.softartdev.notedelight.repository.SafeRepo

class AndroidDatabaseHolder(
    context: Context,
    passphrase: CharSequence,
) : RoomDbHolder {

    val noteDatabase: NoteDatabase = Room
        .databaseBuilder(context, NoteDatabase::class.java, SafeRepo.DB_NAME)
        .openHelperFactory(SafeHelperFactory.fromUser(SpannableStringBuilder(passphrase)))
        .fallbackToDestructiveMigrationOnDowngrade(false)
        .build()

    override fun close() = noteDatabase.close()
}