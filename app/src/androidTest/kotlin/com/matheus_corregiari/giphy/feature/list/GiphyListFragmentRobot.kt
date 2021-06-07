package com.matheus_corregiari.giphy.feature.list

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.matheus_corregiari.giphy.R
import com.matheus_corregiari.giphy.data.RepositoryProvider
import com.matheus_corregiari.giphy.data.model.GiphyImageDTO
import com.matheus_corregiari.giphy.data.model.GiphyImageUrlDTO
import com.matheus_corregiari.giphy.data.model.GiphyItemDTO
import com.matheus_corregiari.giphy.data.repository.GiphyRepository
import com.matheus_corregiari.giphy.delegate.ViewModelProviderDelegate
import com.matheus_corregiari.giphy.feature.GiphyBrowseViewModel
import com.matheus_corregiari.giphy.test.Given
import com.matheus_corregiari.giphy.test.Then
import com.matheus_corregiari.giphy.test.When
import io.mockk.every
import io.mockk.verify

fun GiphyListFragmentTest.given(func: GiphyListFragmentGiven.() -> Unit) =
    GiphyListFragmentGiven().apply(func)

class GiphyListFragmentGiven : Given<GiphyListFragmentWhen, GiphyListFragmentThen> {

    private val repository: GiphyRepository = RepositoryProvider.giphyRepository
    private lateinit var viewModel: GiphyBrowseViewModel

    override fun whenCreator() = GiphyListFragmentWhen(viewModel)

    fun withEmptyList() {
        every { repository.trendingGiphys(false) } returns testPage(emptyList())
        every { repository.searchGiphys("term") } returns testPage(emptyList())
    }

    fun withList() {
        val response = listOf(
            GiphyItemDTO("id", "title", GiphyImageDTO(GiphyImageUrlDTO("imageUrl")))
        )
        every { repository.trendingGiphys(false) } returns testPage(response)
        every { repository.searchGiphys("term") } returns testPage(response)
    }

    fun launchListFragment() {
        FragmentScenario.launchInContainer(
            fragmentClass = GiphyListFragment::class.java,
            fragmentArgs = bundleOf(),
            themeResId = R.style.GiphyApp_Theme,
            factory = null
        ).onFragment {
            viewModel = ViewModelProviderDelegate(GiphyBrowseViewModel::class, true)
                .getValue(it, ::viewModel)
        }
    }
}

class GiphyListFragmentWhen(private val viewModel: GiphyBrowseViewModel) :
    When<GiphyListFragmentThen> {

    override fun thenCreator() = GiphyListFragmentThen()

    fun clickOnFavorite() {
        onView(withId(R.id.favorite_view)).perform(click())
    }

    fun receiveSearchTerm() {
        viewModel.searchTerm("term")
    }

    fun clearSearch() {
        viewModel.searchTerm("")
    }
}

class GiphyListFragmentThen : Then {

    private val repository: GiphyRepository = RepositoryProvider.giphyRepository

    fun listIsDisplayed() {
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()))
    }

    fun trendingWasCalled() {
        verify { repository.trendingGiphys(any()) }
    }

    fun listIsEmpty() {
        onView(withId(R.id.gif_view)).check(doesNotExist())
    }

    fun listIsNotEmpty() {
        onView(withId(R.id.gif_view)).check(matches(isDisplayed()))
    }

    fun itemIsFavorited() {
        verify { repository.favoriteGiphy(any()) }
    }

    fun searchWasCalled() {
        verify { repository.searchGiphys("term") }
    }
}

private fun testPage(result: List<GiphyItemDTO>) = Pager(
    config = PagingConfig(pageSize = 1),
    pagingSourceFactory = { TestSource(result) })

private class TestSource(private val result: List<GiphyItemDTO>) :
    PagingSource<Int, GiphyItemDTO>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GiphyItemDTO> {
        return LoadResult.Page(
            data = result,
            prevKey = null,
            nextKey = null
        )
    }

    override fun getRefreshKey(state: PagingState<Int, GiphyItemDTO>): Int? = null
}
