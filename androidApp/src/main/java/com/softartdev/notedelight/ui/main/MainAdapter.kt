package com.softartdev.notedelight.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softartdev.notedelight.R
import com.softartdev.notedelight.shared.database.Note
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_note.view.*
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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NotesViewHolder(v)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)
    }

    override fun getItemCount(): Int = notes.size

    inner class NotesViewHolder(
            override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(note: Note) = with(containerView) {
            item_note_title_text_view.text = note.title
            item_note_date_text_view.text = simpleDateFormat.format(note.dateModified)
            setOnClickListener { clickListener?.onNoteClick(note.id) }
        }
    }

    interface ClickListener {
        fun onNoteClick(noteId: Long)
    }
}
