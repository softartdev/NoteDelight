package com.softartdev.notedelight.old.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.softartdev.notedelight.old.R
import com.softartdev.notedelight.old.databinding.ActivityMainBinding
import com.softartdev.notedelight.old.ui.base.BaseActivity
import com.softartdev.notedelight.old.ui.note.NoteActivity
import com.softartdev.notedelight.old.ui.signin.SignInActivity
import com.softartdev.notedelight.old.util.gone
import com.softartdev.notedelight.old.util.tintIcon
import com.softartdev.notedelight.old.util.visible
import com.softartdev.notedelight.shared.presentation.main.MainViewModel
import com.softartdev.notedelight.shared.presentation.main.NoteListResult
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity(contentLayoutId = R.layout.activity_main) {
    private val mainViewModel by viewModel<MainViewModel>()
    private val binding by viewBinding(ActivityMainBinding::bind, android.R.id.content)
    private val mainAdapter by lazy { MainAdapter(onNoteClick = this::onNoteClick) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.mainSwipeRefresh.apply {
            setProgressBackgroundColorSchemeResource(R.color.secondary)
            setColorSchemeResources(R.color.on_secondary)
            setOnRefreshListener { mainViewModel.updateNotes() }
        }
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

    private fun onChanged(noteListResult: NoteListResult) = when (noteListResult) {
        is NoteListResult.Loading -> showProgress(true)
        is NoteListResult.Success -> {
            showProgress(false)
            mainAdapter.submitList(noteListResult.result)
            showEmpty(noteListResult.result.isEmpty())
        }
        is NoteListResult.Error -> {
            showProgress(false)
            showError(noteListResult.error)
        }
        is NoteListResult.NavMain -> navSignIn()
    }

    private fun onNoteClick(noteId: Long) = startActivity(NoteActivity.getStartIntent(this, noteId))

    private fun showProgress(show: Boolean) = if (binding.mainSwipeRefresh.isRefreshing) {
        binding.mainSwipeRefresh.isRefreshing = show
    } else with(binding.mainProgressView) { if (show) visible() else gone() }

    private fun showEmpty(show: Boolean) = with(binding.mainEmptyView) {
        if (show) visible() else gone()
    }

    private fun showError(message: String?) = with(binding.mainErrorView) {
        visible()
        messageTextView.text = message
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
