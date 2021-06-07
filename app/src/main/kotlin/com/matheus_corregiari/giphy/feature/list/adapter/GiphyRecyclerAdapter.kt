package com.matheus_corregiari.giphy.feature.list.adapter

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.matheus_corregiari.giphy.feature.list.adapter.viewholder.GiphyFooterViewHolder
import com.matheus_corregiari.giphy.feature.model.GiphyItemDO
import com.matheus_corregiari.giphy.feature.view.GiphyItemView

class GiphyRecyclerAdapter :
    PagingDataAdapter<GiphyItemDO, RecyclerView.ViewHolder>(GiphyRecyclerItemDiffer()) {

    private var listener: ((GiphyItemDO) -> Unit)? = null

    fun withOnFavoriteListener(listener: (GiphyItemDO) -> Unit) = apply { this.listener = listener }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { item ->
            (holder.itemView as GiphyItemView).bind(item)
            (holder.itemView as GiphyItemView).setOnClickListener { listener?.invoke(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : RecyclerView.ViewHolder(GiphyItemView(parent.context)) {}
}

class GiphyLoadRecyclerAdapter(private val adapter: GiphyRecyclerAdapter) :
    LoadStateAdapter<GiphyFooterViewHolder>() {

    override fun onBindViewHolder(holder: GiphyFooterViewHolder, loadState: LoadState) =
        holder.bindTo(loadState)

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
        GiphyFooterViewHolder(parent, adapter::retry)
}

private class GiphyRecyclerItemDiffer : DiffUtil.ItemCallback<GiphyItemDO>() {

    override fun areItemsTheSame(oldItem: GiphyItemDO, newItem: GiphyItemDO) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: GiphyItemDO, newItem: GiphyItemDO) =
        oldItem.favorite == newItem.favorite
}