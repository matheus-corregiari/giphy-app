package com.matheus_corregiari.giphy.feature.list.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.matheus_corregiari.giphy.R

class GiphyListItemDecoration : RecyclerView.ItemDecoration() {

    private var spacing = -1;

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {

        if (spacing <= 0) {
            spacing = view.resources.getDimensionPixelOffset(R.dimen.spacing_medium)
        }
        val layoutManager = parent.layoutManager as? GridLayoutManager ?: return

        val spanCount = layoutManager.spanCount
        val itemPosition = layoutManager.getPosition(view)

        val isLeftItem = itemPosition % spanCount == 0
        outRect.left = if (isLeftItem) spacing else spacing / 2
        outRect.right = if (isLeftItem) spacing / 2 else spacing

        val isTopView = itemPosition < spanCount
        outRect.top = if (isTopView) spacing else spacing / 2
        outRect.bottom = if (isTopView) spacing / 2 else spacing
    }
}
