package com.softartdev.notedelight.usecase.note

import com.softartdev.notedelight.db.NoteDAO
import kotlinx.coroutines.channels.Channel

class DeleteNoteUseCase(private val noteDAO: NoteDAO) : (Long) -> Unit {

    override fun invoke(id: Long) = noteDAO.delete(id)

    companion object {
        val deleteChannel: Channel<Boolean> by lazy { return@lazy Channel<Boolean>() }
    }
}