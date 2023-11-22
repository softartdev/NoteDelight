package com.softartdev.notedelight.shared.usecase.note

import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.softartdev.notedelight.shared.db.NoteDAO
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
        val titleChannel: Channel<String> by lazy { return@lazy Channel<String>() }
    }
}
