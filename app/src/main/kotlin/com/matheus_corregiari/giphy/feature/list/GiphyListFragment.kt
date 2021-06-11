package com.matheus_corregiari.giphy.feature.list

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import br.com.arch.toolkit.delegate.extraProvider
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.recycler.adapter.SimpleAdapter
import com.matheus_corregiari.giphy.R
import com.matheus_corregiari.giphy.delegate.viewModelProvider
import com.matheus_corregiari.giphy.feature.GiphyBrowseViewModel
import com.matheus_corregiari.giphy.feature.list.decoration.GiphyListItemDecoration
import com.matheus_corregiari.giphy.feature.model.GiphyItemDO
import com.matheus_corregiari.giphy.feature.view.GiphyItemView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val EXTRA_SHOW_ONY_FAVORED = "SAVE_STATE_MACHINE_INSTANCE"

fun newGiphyListFragment(displayOnlyFavored: Boolean): GiphyListFragment {
    val arguments = bundleOf(EXTRA_SHOW_ONY_FAVORED to displayOnlyFavored)
    return GiphyListFragment().apply { setArguments(arguments) }
}

class GiphyListFragment : Fragment(R.layout.fragment_giphy_list) {

    private val viewModel by viewModelProvider(GiphyListViewModel::class)
    private val searchViewModel by viewModelProvider(GiphyBrowseViewModel::class, fromParent = true)

    // PAGING
//    private val adapter = GiphyRecyclerAdapter()
//        .withOnFavoriteListener(::onGiphyFavoriteClick)
    // Simple
    private val adapter = SimpleAdapter(::GiphyItemView)
        .withListener(::onGiphyFavoriteClick)

    //region Extras
    private val displayOnlyFavored: Boolean by extraProvider(
        EXTRA_SHOW_ONY_FAVORED,
        default = false
    )
    //endregion

    //region Views
    private val recyclerView: RecyclerView by viewProvider(R.id.recycler_view)
    //endregion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.addItemDecoration(GiphyListItemDecoration())
        (recyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        recyclerView.adapter = adapter
//        .withLoadStateHeaderAndFooter(
//            header = GiphyLoadRecyclerAdapter(adapter),
//            footer = GiphyLoadRecyclerAdapter(adapter)
//        )
        searchViewModel.searchLiveData.observe(viewLifecycleOwner, ::onNewSearchTermReceive)


        if (displayOnlyFavored.not()) {
            viewModel.testeLive().observe(viewLifecycleOwner) {
                data(observer = adapter::setList)
                loading {  }
            }

//            viewLifecycleOwner.lifecycleScope.launch {
//                viewModel.testeFlow().collectLatest {
//                    Log.wtf("TEST FLOW", it.toString())
//                }
//            }
        }
    }

    override fun onResume() {
        super.onResume()
//        searchViewModel.lastTypedTerm
//            .takeIf { it.isNullOrBlank().not() && displayOnlyFavored.not() }
//            ?.run(::onNewSearchTermReceive)
//            ?: fetchTrending()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.adapter = null
    }

    private fun onNewSearchTermReceive(term: String?) {
        if (displayOnlyFavored) return
        observeFLow(viewModel.searchTerm(term))
    }

    private fun fetchTrending() = observeFLow(viewModel.fetchList(displayOnlyFavored))

    private fun onGiphyFavoriteClick(itemDO: GiphyItemDO) {
        viewModel.favorite(itemDO)
//        adapter.refresh()
    }

    private fun observeFLow(flow: Flow<PagingData<GiphyItemDO>>) {
        viewLifecycleOwner.lifecycleScope.launch {
//            flow.collectLatest(adapter::submitData)
        }
    }
}