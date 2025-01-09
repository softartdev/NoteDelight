package com.softartdev.notedelight.usecase.note

import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.util.createLocalDateTime
import kotlinx.coroutines.channels.Channel

class SaveNoteUseCase(private val noteDAO: NoteDAO) : (Long, String, String) -> Unit {

    override fun invoke(id: Long, title: String, text: String) {
        val note = noteDAO.load(id).copy(
            title = title,
            text = text,
            dateModified = createLocalDateTime()
        )
        noteDAO.update(note)
    }

    companion object {
        val dialogChannel: Channel<Boolean?> by lazy { return@lazy Channel<Boolean?>() }
    }
}
