package com.matheus_corregiari.giphy.feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.viewpager.widget.ViewPager
import br.com.arch.toolkit.delegate.viewProvider
import com.google.android.material.tabs.TabLayout
import com.matheus_corregiari.giphy.R
import com.matheus_corregiari.giphy.delegate.viewModelProvider
import com.matheus_corregiari.giphy.extension.enabledRecursively
import com.matheus_corregiari.giphy.util.SearchTextListener

class GiphyBrowseActivity : AppCompatActivity(R.layout.activity_giphy) {

    private val viewModel by viewModelProvider(GiphyBrowseViewModel::class)

    //region Views
    private val searchView: SearchView by viewProvider(R.id.search_view)
    private val tabLayout: TabLayout by viewProvider(R.id.tab_layout)
    private val viewPager: ViewPager by viewProvider(R.id.view_pager)
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tabLayout.setupWithViewPager(viewPager, true)
        viewPager.adapter = GiphyBrowsePagerAdapter(resources, supportFragmentManager)
        viewPager.isNestedScrollingEnabled = true
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) =
                searchView.enabledRecursively(position == 0)

            override fun onPageScrolled(position: Int, offset: Float, offsetPixels: Int) = Unit
            override fun onPageScrollStateChanged(state: Int) = Unit
        })

        searchView.setOnQueryTextListener(SearchTextListener(onSearch = ::onGiphySearch))
    }

    private fun onGiphySearch(term: String) = viewModel.searchTerm(term)
}