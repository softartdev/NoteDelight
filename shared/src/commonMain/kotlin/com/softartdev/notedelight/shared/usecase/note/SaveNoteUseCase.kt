package com.softartdev.notedelight.shared.usecase.note

import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.softartdev.notedelight.shared.db.NoteDAO
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
        val saveChannel: Channel<Boolean> by lazy { return@lazy Channel<Boolean>() }
    }
}
