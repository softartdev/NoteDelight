package com.softartdev.notedelight.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.softartdev.notedelight.R
import com.softartdev.notedelight.databinding.ActivityMainBinding
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.ui.base.BaseActivity
import com.softartdev.notedelight.ui.note.NoteActivity
import com.softartdev.notedelight.ui.signin.SignInActivity
import com.softartdev.notedelight.util.autoCleared
import com.softartdev.notedelight.util.gone
import com.softartdev.notedelight.util.tintIcon
import com.softartdev.notedelight.util.visible
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity(
        contentLayoutId = R.layout.activity_main
), MainAdapter.ClickListener, Observer<NoteListResult> {

    private val mainViewModel by viewModel<MainViewModel>()
    private val binding by viewBinding(ActivityMainBinding::bind, android.R.id.content)
    private var mainAdapter by autoCleared<MainAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.mainSwipeRefresh.apply {
            setProgressBackgroundColorSchemeResource(R.color.secondary)
            setColorSchemeResources(R.color.on_secondary)
            setOnRefreshListener { mainViewModel.updateNotes() }
        }
        mainAdapter = MainAdapter()
        mainAdapter.clickListener = this
        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mainAdapter
        }
        binding.addNoteFab.setOnClickListener {
            startActivity(NoteActivity.getStartIntent(this, 0L))
        }
        binding.mainErrorView.reloadButton.setOnClickListener { mainViewModel.updateNotes() }
        lifecycleScope.launch {
            mainViewModel.resultStateFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect(::onChanged)
        }
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
        if (binding.mainSwipeRefresh.isRefreshing) {
            binding.mainSwipeRefresh.isRefreshing = show
        } else {
            binding.mainProgressView.apply { if (show) visible() else gone() }
        }
    }

    private fun showEmpty(show: Boolean) {
        binding.mainEmptyView.apply { if (show) visible() else gone() }
    }

    private fun showError(message: String?) {
        binding.mainErrorView.apply {
            visible()
            messageTextView.text = message
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
