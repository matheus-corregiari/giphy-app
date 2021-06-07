package com.matheus_corregiari.giphy.feature

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.matheus_corregiari.giphy.test.MockRepositoriesRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GiphyBrowseActivityTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mockRepositoriesRule = MockRepositoriesRule()

    @Test
    fun whenLaunch_shouldDisplayBrowseTabAndTheFavoriteTab() {
        given {
            launchBrowseActivity()
        } then {
            browseTabIsDisplayed()
            favoriteTabIsDisplayed()
            searchFieldIsEnabled()
        }
    }

    @Test
    fun onBrowseTab_shouldEnabledSearchField() {
        given {
            launchBrowseActivity()
        } `when` {
            selectBrowseTab()
        } then {
            searchFieldIsEnabled()
        }
    }

    @Test
    fun onFavoriteTab_shouldDisableSearchField() {
        given {
            launchBrowseActivity()
        } `when` {
            selectFavoriteTab()
        } then {
            searchFieldIsNotEnabled()
        }
    }

    @Test
    fun whenSearch_shouldUpdateInfoOnViewModel() {
        given {
            launchBrowseActivity()
        } `when` {
            makeSearch()
        } then {
            searchTermIsOnViewModel()
        }
    }
}
