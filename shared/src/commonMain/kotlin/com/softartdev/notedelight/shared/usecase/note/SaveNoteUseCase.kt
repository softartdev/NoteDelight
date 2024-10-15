package com.softartdev.notedelight.shared.usecase.note

import com.softartdev.notedelight.shared.date.createLocalDateTime
import com.softartdev.notedelight.shared.db.NoteDAO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow

class SaveNoteUseCase(private val noteDAO: NoteDAO) : (Long, String, String) -> Unit {

    override fun invoke(id: Long, title: String, text: String) {
        val note = noteDAO.load(id).copy(
            title = title,
            text = text,
            dateModified = createLocalDateTime()
        )
        noteDAO.update(note)
    }

    suspend fun receiveDialogResult(): Boolean {
        return dialogResultChannel.receiveAsFlow().first()
    }

    suspend fun sendDialogResult(result: Boolean) {
        dialogResultChannel.send(result)
    }

    companion object {
        private val dialogResultChannel = Channel<Boolean>(Channel.CONFLATED)
    }
}
