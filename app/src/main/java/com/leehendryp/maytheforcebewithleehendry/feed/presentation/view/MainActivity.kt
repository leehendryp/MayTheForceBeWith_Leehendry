package com.leehendryp.maytheforcebewithleehendry.feed.presentation.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
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
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.Success
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.Error
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.Search
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedState.Loading
import com.leehendryp.maytheforcebewithleehendry.feed.presentation.viewmodel.FeedViewModel
import javax.inject.Inject
import androidx.appcompat.widget.SearchView.OnQueryTextListener

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val feedViewModel: FeedViewModel by viewModels { viewModelFactory }

    private lateinit var feedAdapter: FeedAdapter
    private lateinit var binding: ActivityMainBinding

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
            onSaveFavorite = { saveFavorite(it) }
        )

        binding.recyclerviewCharacters.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = feedAdapter
            doOnScrollToEnd { feedViewModel.fetchPeople() }
        }
    }

    private fun initSearchView() {
        binding.searchBar
            .apply {
                doOnQuerySubmit { feedViewModel.searchCharacterBy(it) }
                setOnQueryTextFocusChangeListener { _, hasFocus -> if (!hasFocus) feedViewModel.fetchPeople() }
            }
    }

    private fun injectDependencies() =
        (application as MayTheFoceBeWithThisApplication).appComponent.inject(this)

    private fun observeViewModel() {
        feedViewModel.state.observe(this, Observer(::updateUI))
    }

    private fun updateUI(state: FeedState) {
        toggleLoading()
        when (state) {
            Search -> clearAdapterList()
            is Success -> updateAdapterData()
            is Error -> showErrorMessage()
        }
    }

    private fun clearAdapterList() = feedAdapter.clearList()

    private fun updateAdapterData() {
        feedAdapter.update((feedViewModel.state.value as Success).data.toSet())
    }

    private fun showErrorMessage() {
        Toast.makeText(this, getString(R.string.error_message), Toast.LENGTH_LONG).show()
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

    private fun saveFavorite(character: Character) = feedViewModel.saveFavorite(character)

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
}
