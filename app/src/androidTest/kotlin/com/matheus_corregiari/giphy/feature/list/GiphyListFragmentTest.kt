package com.matheus_corregiari.giphy.feature.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.matheus_corregiari.giphy.test.MockRepositoriesRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GiphyListFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mockRepositoriesRule = MockRepositoriesRule()

    @Test
    fun whenLaunch_shouldDisplayListAndCallTrendingGifs() {
        given {
            withEmptyList()
            launchListFragment()
        } then {
            listIsDisplayed()
            trendingWasCalled()
            listIsEmpty()
        }
    }

    @Test
    fun whenLaunchWithList_shouldDisplayListAndCallTrendingGifs() {
        given {
            withList()
            launchListFragment()
        } then {
            listIsDisplayed()
            trendingWasCalled()
            listIsNotEmpty()
        }
    }

    @Test
    fun clickOnFavorite_shouldFavoriteItem() {
        given {
            withList()
            launchListFragment()
        } `when` {
            clickOnFavorite()
        } then {
            itemIsFavorited()
        }
    }

    @Test
    fun receiveSearchTerm_shouldSearch() {
        given {
            withEmptyList()
            launchListFragment()
        } `when` {
            receiveSearchTerm()
        } then {
            searchWasCalled()
        }
    }

    @Test
    fun afterSearch_receiveEmptyTerm_shouldShowTrending() {
        given {
            withEmptyList()
            launchListFragment()
        } `when` {
            receiveSearchTerm()
            clearSearch()
        } then {
            searchWasCalled()
            trendingWasCalled()
        }
    }
}
