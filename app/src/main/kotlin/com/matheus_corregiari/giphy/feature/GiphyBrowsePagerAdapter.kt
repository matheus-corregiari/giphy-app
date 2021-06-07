package com.matheus_corregiari.giphy.feature

import android.content.res.Resources
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.matheus_corregiari.giphy.R
import com.matheus_corregiari.giphy.feature.list.newGiphyListFragment

class GiphyBrowsePagerAdapter(private val resources: Resources, fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int) =
        if (position == 0) newGiphyListFragment(false) else newGiphyListFragment(true)

    override fun getCount() = 2

    override fun getPageTitle(position: Int) =
        resources.getString(if (position == 0) R.string.giphy_list_page_name else R.string.favorite_list_page_name)

}