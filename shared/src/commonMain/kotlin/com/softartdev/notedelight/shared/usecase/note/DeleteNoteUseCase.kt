package com.softartdev.notedelight.shared.usecase.note

import com.softartdev.notedelight.shared.db.NoteDAO
import kotlinx.coroutines.channels.Channel

class DeleteNoteUseCase(private val noteDAO: NoteDAO) : (Long) -> Unit {

    override fun invoke(id: Long) = noteDAO.delete(id)

    companion object {
        val deleteChannel: Channel<Boolean> by lazy { return@lazy Channel<Boolean>() }
    }
}