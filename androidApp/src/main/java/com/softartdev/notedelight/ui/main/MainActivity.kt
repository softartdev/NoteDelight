package com.softartdev.notedelight.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.softartdev.notedelight.R
import com.softartdev.notedelight.shared.database.Note
import com.softartdev.notedelight.ui.base.BaseActivity
import com.softartdev.notedelight.ui.note.NoteActivity
import com.softartdev.notedelight.ui.signin.SignInActivity
import com.softartdev.notedelight.util.autoCleared
import com.softartdev.notedelight.util.gone
import com.softartdev.notedelight.util.tintIcon
import com.softartdev.notedelight.util.visible
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_error.view.*
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.scope.viewModel

class MainActivity : BaseActivity(
        contentLayoutId = R.layout.activity_main
), MainAdapter.ClickListener, Observer<NoteListResult> {

    private val mainViewModel by lifecycleScope.viewModel<MainViewModel>(this)
    private var mainAdapter by autoCleared<MainAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        main_swipe_refresh.apply {
            setProgressBackgroundColorSchemeResource(R.color.secondary)
            setColorSchemeResources(R.color.on_secondary)
            setOnRefreshListener { mainViewModel.updateNotes() }
        }
        mainAdapter = MainAdapter()
        mainAdapter.clickListener = this
        notes_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mainAdapter
        }
        add_note_fab.setOnClickListener {
            startActivity(NoteActivity.getStartIntent(this, 0L))
        }
        main_error_view.button_reload.setOnClickListener { mainViewModel.updateNotes() }
        mainViewModel.resultLiveData.observe(this, this)
        mainViewModel.updateNotes()
    }

    override fun onChanged(noteListResult: NoteListResult) = when (noteListResult) {
        NoteListResult.Loading -> showProgress(true)
        is NoteListResult.Success -> {
            showProgress(false)
            onUpdateNotes(noteListResult.result)
            showEmpty(noteListResult.result.isEmpty())
        }
        is NoteListResult.Error -> {
            showProgress(false)
            showError(noteListResult.error)
        }
        NoteListResult.NavMain -> navSignIn()
    }

    private fun onUpdateNotes(noteList: List<Note>) {
        mainAdapter.notes = noteList
    }

    override fun onNoteClick(noteId: Long) {
        startActivity(NoteActivity.getStartIntent(this, noteId))
    }

    private fun showProgress(show: Boolean) {
        if (main_swipe_refresh.isRefreshing) {
            main_swipe_refresh.isRefreshing = show
        } else {
            main_progress_view.apply { if (show) visible() else gone() }
        }
    }

    private fun showEmpty(show: Boolean) {
        main_empty_view.apply { if (show) visible() else gone() }
    }

    private fun showError(message: String?) {
        main_error_view.apply {
            visible()
            text_error_message.text = message
        }
    }

    private fun navSignIn() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.action_settings).tintIcon(this)
        return true
    }

}
