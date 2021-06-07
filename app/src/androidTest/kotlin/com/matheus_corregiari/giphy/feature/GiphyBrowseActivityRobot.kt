package com.matheus_corregiari.giphy.feature

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.matheus_corregiari.giphy.R
import com.matheus_corregiari.giphy.delegate.ViewModelProviderDelegate
import com.matheus_corregiari.giphy.test.Given
import com.matheus_corregiari.giphy.test.Then
import com.matheus_corregiari.giphy.test.When
import kotlin.test.assertTrue
import org.hamcrest.Matchers.not

fun GiphyBrowseActivityTest.given(func: GiphyBrowseActivityGiven.() -> Unit) =
    GiphyBrowseActivityGiven().apply(func)

class GiphyBrowseActivityGiven : Given<GiphyBrowseActivityWhen, GiphyBrowseActivityThen> {

    private lateinit var viewModel: GiphyBrowseViewModel

    override fun whenCreator() = GiphyBrowseActivityWhen(viewModel)

    fun launchBrowseActivity() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), GiphyBrowseActivity::class.java)
        ActivityScenario.launch<GiphyBrowseActivity>(intent)
            .onActivity {
                viewModel = ViewModelProviderDelegate(GiphyBrowseViewModel::class, false)
                    .getValue(it, ::viewModel)
            }
    }
}

class GiphyBrowseActivityWhen(private val viewModel: GiphyBrowseViewModel) :
    When<GiphyBrowseActivityThen> {
    override fun thenCreator() = GiphyBrowseActivityThen(viewModel)

    fun selectBrowseTab() {
        onView(withText(R.string.giphy_list_page_name)).perform(click())
    }

    fun selectFavoriteTab() {
        onView(withText(R.string.favorite_list_page_name)).perform(click())
    }

    fun makeSearch() {
        onView(withId(R.id.search_src_text)).perform(typeText("term"))
    }
}

class GiphyBrowseActivityThen(private val viewModel: GiphyBrowseViewModel) : Then {
    fun browseTabIsDisplayed() {
        onView(withText(R.string.giphy_list_page_name)).check(matches(isDisplayed()))
    }

    fun favoriteTabIsDisplayed() {
        onView(withText(R.string.favorite_list_page_name)).check(matches(isDisplayed()))
    }

    fun searchFieldIsEnabled() {
        onView(withId(R.id.search_view)).check(matches(isEnabled()))
    }

    fun searchFieldIsNotEnabled() {
        onView(withId(R.id.search_view)).check(matches(not(isEnabled())))
    }

    fun searchTermIsOnViewModel() {
        Thread.sleep(300L)
        assertTrue(viewModel.lastTypedTerm.isNullOrBlank().not())
        assertTrue(viewModel.lastTypedTerm == "term")
    }
}