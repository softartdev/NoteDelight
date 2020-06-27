package com.softartdev.notedelight.ui.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.softartdev.notedelight.R
import com.softartdev.notedelight.ui.base.BaseActivity
import com.softartdev.notedelight.ui.title.EditTitleDialog
import com.softartdev.notedelight.util.*
import kotlinx.android.synthetic.main.activity_note.*
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel

class NoteActivity : BaseActivity(R.layout.activity_note), Observer<NoteResult> {

    private val noteViewModel by lifecycleScope.viewModel<NoteViewModel>(this)

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
        get() = note_edit_text.text.toString()
        set(value) = note_edit_text.setText(value)

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
        noteViewModel.resultLiveData.observe(this, this)
    }

    override fun onChanged(noteResult: NoteResult) {
        note_progress_bar.invisible()
        when (noteResult) {
            NoteResult.Loading -> note_progress_bar.visible()
            is NoteResult.Created -> note_edit_text.showKeyboard()
            is NoteResult.Loaded -> {
                noteTitle = noteResult.result.title
                noteText = noteResult.result.text
            }
            is NoteResult.Saved -> {
                val noteSaved = getString(R.string.note_saved) + ": " + noteResult.title
                Snackbar.make(note_edit_text, noteSaved, Snackbar.LENGTH_LONG).show()
            }
            is NoteResult.NavEditTitle -> {
                val editTitleDialog = EditTitleDialog.create(noteResult.noteId)
                editTitleDialog.show(supportFragmentManager, "EDIT_TITLE_DIALOG_TAG")
            }
            is NoteResult.TitleUpdated -> {
                noteTitle = noteResult.title
            }
            NoteResult.Empty -> {
                Snackbar.make(note_edit_text, R.string.note_empty, Snackbar.LENGTH_LONG).show()
            }
            NoteResult.Deleted -> {
                Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_LONG).show()
                onNavBack()
            }
            NoteResult.CheckSaveChange -> onCheckSaveChange()
            NoteResult.NavBack -> onNavBack()
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
