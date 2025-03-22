package com.softartdev.notedelight.usecase.note

import com.softartdev.notedelight.db.NoteDAO
import com.softartdev.notedelight.util.createLocalDateTime
import kotlinx.coroutines.channels.Channel

class UpdateTitleUseCase(private val noteDAO: NoteDAO) : (Long, String) -> Unit {

    override fun invoke(id: Long, title: String) {
        val note = noteDAO.load(id).copy(
            title = title,
            dateModified = createLocalDateTime()
        )
        noteDAO.update(note)
    }

    companion object {
        val dialogChannel: Channel<String?> by lazy { return@lazy Channel<String?>() }
    }
}
