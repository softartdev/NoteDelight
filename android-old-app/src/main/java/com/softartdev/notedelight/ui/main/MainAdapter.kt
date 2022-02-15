package com.softartdev.notedelight.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softartdev.notedelight.databinding.ItemNoteBinding
import com.softartdev.notedelight.shared.date.toJvmDate
import com.softartdev.notedelight.shared.db.Note
import java.text.SimpleDateFormat
import java.util.*

class MainAdapter : RecyclerView.Adapter<MainAdapter.NotesViewHolder>() {

    var notes: List<Note> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var clickListener: ClickListener? = null

    private val simpleDateFormat = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)
    }

    override fun getItemCount(): Int = notes.size

    inner class NotesViewHolder(
        private val binding: ItemNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) = with(binding) {
            itemNoteTitleTextView.text = note.title
            itemNoteDateTextView.text = simpleDateFormat.format(note.dateModified.toJvmDate())
            root.setOnClickListener { clickListener?.onNoteClick(note.id) }
        }
    }

    interface ClickListener {
        fun onNoteClick(noteId: Long)
    }
}
