package com.matheus_corregiari.giphy.feature.list

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import br.com.arch.toolkit.delegate.extraProvider
import br.com.arch.toolkit.delegate.viewProvider
import br.com.arch.toolkit.statemachine.ViewStateMachine
import br.com.arch.toolkit.statemachine.config
import br.com.arch.toolkit.statemachine.setup
import br.com.arch.toolkit.statemachine.state
import com.matheus_corregiari.giphy.R
import com.matheus_corregiari.giphy.delegate.viewModelProvider
import com.matheus_corregiari.giphy.feature.GiphyBrowseViewModel
import com.matheus_corregiari.giphy.feature.list.adapter.SimpleGiphyRecyclerAdapter
import com.matheus_corregiari.giphy.feature.list.decoration.GiphyListItemDecoration
import com.matheus_corregiari.giphy.feature.model.GiphyItemDO
import kotlinx.coroutines.flow.collect

private const val EXTRA_SHOW_ONY_FAVORED = "SAVE_STATE_MACHINE_INSTANCE"

private const val STATE_LOADING = 1
private const val STATE_ERROR = 2
private const val STATE_LIST = 3
private const val STATE_EMPTY = 4

fun newGiphyListFragment(displayOnlyFavored: Boolean): GiphyListFragment {
    val arguments = bundleOf(EXTRA_SHOW_ONY_FAVORED to displayOnlyFavored)
    return GiphyListFragment().apply { setArguments(arguments) }
}

class GiphyListFragment : Fragment(R.layout.fragment_giphy_list) {

    private val viewModel by viewModelProvider(GiphyListViewModel::class)
    private val searchViewModel by viewModelProvider(GiphyBrowseViewModel::class, fromParent = true)
    private val stateMachine = ViewStateMachine()

    private val adapter = SimpleGiphyRecyclerAdapter().withListener(::onGiphyFavoriteClick)

    //region Extras
    private val displayOnlyFavored: Boolean by extraProvider(
        EXTRA_SHOW_ONY_FAVORED,
        default = false
    )
    //endregion

    //region Views
    private val root: ViewGroup by viewProvider(R.id.list_root)
    private val swipeRefreshLayout: SwipeRefreshLayout by viewProvider(R.id.swipe_refresh)
    private val recyclerView: RecyclerView by viewProvider(R.id.recycler_view)
    private val errorView: View by viewProvider(R.id.error_view)
    private val loadingView: View by viewProvider(R.id.loading_view)
    //endregion

    private val transition: Transition
        get() = AutoTransition().removeTarget(recyclerView).excludeChildren(recyclerView, true)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.setOnRefreshListener {
            // TODO discover a way to force refresh
        }

        // TODO paging
        recyclerView.addItemDecoration(GiphyListItemDecoration())
        (recyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        recyclerView.adapter = adapter

        searchViewModel.searchLiveData.observe(viewLifecycleOwner, ::onNewSearchTermReceive)
        if (displayOnlyFavored.not()) {
            viewModel.testeLive().observe(viewLifecycleOwner) {

                showLoading(withData = false) { stateMachine.changeState(STATE_LOADING) }
                error(withData = false) { _ -> stateMachine.changeState(STATE_ERROR) }

                loading(withData = true) { swipeRefreshLayout.isRefreshing = it }
                error(withData = true) { _ ->
                    Toast.makeText(root.context, "Error refreshing", Toast.LENGTH_SHORT).show()
                }

                data(observer = ::onGiphyListReceive)
            }
        } else {
            lifecycle.coroutineScope.launchWhenCreated {
                viewModel.testeFlow().collect { onGiphyListReceive(it) }
            }
        }
        setupStateMachine()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.adapter = null
    }

    private fun setupStateMachine() = stateMachine.setup {
        config {
            initialState = STATE_LOADING
            setOnChangeState { TransitionManager.beginDelayedTransition(root, transition) }
        }
        state(STATE_LOADING) {
            visibles(loadingView)
            gones(swipeRefreshLayout, errorView)
        }
        state(STATE_ERROR) {
            visibles(errorView)
            gones(swipeRefreshLayout, loadingView)
        }
        state(STATE_LIST) {
            visibles(swipeRefreshLayout)
            gones(errorView, loadingView)
        }
        state(STATE_EMPTY) {
            gones(swipeRefreshLayout, errorView, loadingView)
        }
    }

    private fun onNewSearchTermReceive(term: String?) {
        if (displayOnlyFavored) return
    }

    private fun onGiphyListReceive(list: List<GiphyItemDO>) {
        adapter.setList(list)
        recyclerView.invalidateItemDecorations()
        if (adapter.itemCount == 0) {
            stateMachine.changeState(STATE_EMPTY)
        } else {
            stateMachine.changeState(STATE_LIST)
        }
    }

    private fun onGiphyFavoriteClick(itemDO: GiphyItemDO) {
        viewModel.favorite(itemDO)
    }
}