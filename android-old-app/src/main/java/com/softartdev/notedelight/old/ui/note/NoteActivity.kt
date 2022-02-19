package com.softartdev.notedelight.old.ui.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.softartdev.notedelight.old.R
import com.softartdev.notedelight.old.databinding.ActivityNoteBinding
import com.softartdev.notedelight.old.ui.base.BaseActivity
import com.softartdev.notedelight.old.ui.title.EditTitleDialog
import com.softartdev.notedelight.old.util.*
import com.softartdev.notedelight.shared.presentation.note.NoteResult
import com.softartdev.notedelight.shared.presentation.note.NoteViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class NoteActivity : BaseActivity(R.layout.activity_note), Observer<NoteResult> {

    private val noteViewModel by viewModel<NoteViewModel>()
    private val binding by viewBinding(ActivityNoteBinding::bind, R.id.note_linear_layout)

    private val noteId: Long
        get() = intent.getLongExtra(NOTE_ID, 0L)

    private var noteTitle: String?
        get() = when (val actionBarTitle = supportActionBar?.title?.toString().orEmpty()) {
            getString(R.string.title_activity_note) -> null
            else -> actionBarTitle
        }
        set(value) {
            supportActionBar?.title = value
        }

    private var noteText: String
        get() = binding.noteEditText.text.toString()
        set(value) = binding.noteEditText.setText(value)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        savedInstanceState?.let { bundle ->
            bundle.getString(KEY_TITLE)?.let { noteTitle = it }
            bundle.getString(KEY_TEXT)?.let { noteText = it }
        } ?: when (noteId) {
            0L -> noteViewModel.createNote()
            else -> noteViewModel.loadNote(noteId)
        }
        lifecycleScope.launch {
            noteViewModel.resultStateFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect(::onChanged)
        }
    }

    override fun onChanged(noteResult: NoteResult) {
        binding.noteProgressBar.invisible()
        when (noteResult) {
            is NoteResult.Loading -> binding.noteProgressBar.visible()
            is NoteResult.Created -> binding.noteEditText.showKeyboard()
            is NoteResult.Loaded -> {
                noteTitle = noteResult.result.title
                noteText = noteResult.result.text
            }
            is NoteResult.Saved -> {
                val noteSaved = getString(R.string.note_saved) + ": " + noteResult.title
                Snackbar.make(binding.noteEditText, noteSaved, Snackbar.LENGTH_LONG).show()
            }
            is NoteResult.NavEditTitle -> {
                val editTitleDialog = EditTitleDialog.create(noteResult.noteId)
                editTitleDialog.show(supportFragmentManager, "EDIT_TITLE_DIALOG_TAG")
            }
            is NoteResult.TitleUpdated -> {
                noteTitle = noteResult.title
            }
            is NoteResult.Empty -> {
                Snackbar.make(binding.noteEditText, R.string.note_empty, Snackbar.LENGTH_LONG).show()
            }
            is NoteResult.Deleted -> {
                Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_LONG).show()
                onNavBack()
            }
            is NoteResult.CheckSaveChange -> onCheckSaveChange()
            is NoteResult.NavBack -> onNavBack()
            is NoteResult.Error -> showError(noteResult.message)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_note, menu)
        val menuIconColor = getThemeColor(this, android.R.attr.textColorPrimary)
        menu.findItem(R.id.action_save_note).tintIcon(this, menuIconColor)
        menu.findItem(R.id.action_edit_title).tintIcon(this, menuIconColor)
        menu.findItem(R.id.action_delete_note).tintIcon(this, menuIconColor)
        menu.findItem(R.id.action_settings).tintIcon(this, menuIconColor)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            checkSaveChange()
            true
        }
        R.id.action_save_note -> {
            noteViewModel.saveNote(noteTitle, noteText)
            true
        }
        R.id.action_edit_title -> {
            noteViewModel.editTitle()
            true
        }
        R.id.action_delete_note -> {
            showDeleteDialog()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = checkSaveChange()

    private fun checkSaveChange() = noteViewModel.checkSaveChange(noteTitle, noteText)

    private fun showDeleteDialog() = with(AlertDialog.Builder(this)) {
        setTitle(R.string.action_delete_note)
        setMessage(R.string.note_delete_dialog_message)
        setPositiveButton(android.R.string.ok) { _, _ -> noteViewModel.deleteNote() }
        setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
        show()
    }

    private fun onCheckSaveChange() {
        hideKeyboard()
        with(AlertDialog.Builder(this)) {
            setTitle(R.string.note_changes_not_saved_dialog_title)
            setMessage(R.string.note_save_change_dialog_message)
            setPositiveButton(R.string.yes) { _, _ ->
                noteViewModel.saveNoteAndNavBack(noteTitle, noteText)
            }
            setNegativeButton(R.string.no) { _, _ -> noteViewModel.doNotSaveAndNavBack() }
            setNeutralButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
            show()
        }
    }

    private fun onNavBack() = finish()

    private fun showError(message: String?) = with(AlertDialog.Builder(this)) {
        setTitle(android.R.string.dialog_alert_title)
        setMessage(message)
        setNeutralButton(android.R.string.cancel, null)
        show(); Unit
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_TITLE, noteTitle)
        outState.putString(KEY_TEXT, noteText)
        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val KEY_TITLE = "key_title"
        private const val KEY_TEXT = "key_text"

        const val NOTE_ID = "note_id"

        fun getStartIntent(context: Context, noteId: Long): Intent =
                Intent(context, NoteActivity::class.java)
                        .putExtra(NOTE_ID, noteId)
    }
}
