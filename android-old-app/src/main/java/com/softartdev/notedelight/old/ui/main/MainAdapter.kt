package com.softartdev.notedelight.old.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.softartdev.notedelight.old.databinding.ItemNoteBinding
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.util.CustomDateFormat

class MainAdapter(
    private val onNoteClick: (noteId: Long) -> Unit
) : ListAdapter<Note, NotesViewHolder>(NoteDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) = with(holder.binding) {
        val note = getItem(position)
        root.setOnClickListener { onNoteClick(note.id) }
        itemNoteTitleTextView.text = note.title
        itemNoteDateTextView.text = CustomDateFormat.format(note.dateModified)
    }
}

class NotesViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

object NoteDiff : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem == newItem
}
