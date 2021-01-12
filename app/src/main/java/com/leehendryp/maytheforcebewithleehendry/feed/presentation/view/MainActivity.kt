package com.leehendryp.maytheforcebewithleehendry.feed.presentation.view

import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leehendryp.maytheforcebewithleehendry.R
import com.leehendryp.maytheforcebewithleehendry.core.MayTheFoceBeWithThisApplication
import com.leehendryp.maytheforcebewithleehendry.core.extensions.fadeIn
import com.leehendryp.maytheforcebewithleehendry.core.extensions.vanish
import com.leehendryp.maytheforcebewithleehendry.databinding.ActivityMainBinding
import com.leehendryp.maytheforcebewithleehendry.details.DetailsActivity
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character.Companion.CHARACTER
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedAction
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedAction.CloseFailureDialog
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedAction.LoadMore
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.Failure
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.Loading
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.ContentLoaded
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val feedViewModel: FeedViewModel by viewModels { viewModelFactory }

    private lateinit var feedAdapter: FeedAdapter
    private lateinit var binding: ActivityMainBinding

    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        injectDependencies()
        observeViewModel()
        initRecyclerView()
        initSearchView()
    }

    private fun initRecyclerView() {
        feedAdapter = FeedAdapter(mutableSetOf(),
            onClick = { showCharacterDetails(it) },
            onSaveFavorite = { showCharacterDetails(it) }
        )

        binding.recyclerviewCharacters.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = feedAdapter
        }

        binding.floatingButton.setOnClickListener {
            feedViewModel.dispatch(LoadMore)
        }
    }

    private fun initSearchView() {
        //binding.searchBar.apply {
        //        doOnQuerySubmit { feedViewModel.searchCharacterBy(it) }
        //        setOnQueryTextFocusChangeListener { _, hasFocus -> if (!hasFocus) feedViewModel.fetchPeople() }
        //        }
    }

    private fun injectDependencies() =
        (application as MayTheFoceBeWithThisApplication).appComponent.inject(this)

    private fun observeViewModel() {
        feedViewModel.state.observe(this, Observer(::updateUI))
    }

    private fun updateUI(state: FeedState) {
        toggleLoading()
        when (state) {
            is ContentLoaded -> updateList(state)
            is Failure -> showErrorDialog()
            else -> Unit
        }
    }

    private fun showErrorDialog() {
        dialog = createAlertDialog(
            positiveListener = OnClickListener { dialog, _ ->
                feedViewModel.dispatch(CloseFailureDialog)
                dialog.dismiss()
            }
        )

        dialog?.show()
    }

    private fun clearAdapterList() = feedAdapter.clearList()

    private fun updateList(state: ContentLoaded) {
        feedAdapter.update(state.content.characters)
    }

    private fun toggleLoading() {
        val animDuration: Long = 700
        binding.containerLoadingWheel.apply {
            if (feedViewModel.state.value == Loading) fadeIn(animDuration) else vanish(animDuration)
        }
    }

    private fun showCharacterDetails(character: Character) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra(CHARACTER, character)
        startActivity(intent)
    }

    //private fun saveFavorite(character: Character) = feedViewModel.saveFavorite(character)

    private fun RecyclerView.doOnScrollToEnd(onLoadMore: () -> Unit) {
        this.addOnScrollListener(EndlessOnScrollListener(onLoadMore))
    }

    private fun SearchView.doOnQuerySubmit(block: (String) -> Unit) {
        setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { block(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    private fun createAlertDialog(
        @StringRes title: Int? = R.string.error_title,
        @StringRes message: Int? = R.string.error_retry,
        @StringRes positiveButton: Int? = android.R.string.ok,
        positiveListener: OnClickListener? = null,
        @StringRes neutralButton: Int? = null,
        neutralListener: OnClickListener? = null,
        @StringRes negativeButton: Int? = null,
        negativeListener: OnClickListener? = null
    ): AlertDialog? {

        val alertDialog = AlertDialog.Builder(this).setCancelable(false)

        with(alertDialog) {
            title?.let { setTitle(it) }
            message?.let { setMessage(it) }
            positiveButton?.let { setPositiveButton(it, positiveListener) }
            neutralButton?.let { setNeutralButton(it, neutralListener) }
            negativeButton?.let { setNegativeButton(it, negativeListener) }
        }

        dialog = alertDialog?.create()

        return dialog
    }

    override fun onDestroy() {
        dialog?.dismiss()
        super.onDestroy()
    }
}
