package com.matheus_corregiari.giphy.feature.list.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.matheus_corregiari.giphy.R

class GiphyFooterViewHolder(
    parent: ViewGroup,
    private val retryCallback: () -> Unit
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.view_giphy_footer_item, parent, false)
) {
    private val progressBar = itemView.findViewById<ProgressBar>(R.id.loading_view)
    private val errorMsg = itemView.findViewById<AppCompatTextView>(R.id.error_message_view)
    private val retry = itemView.findViewById<View>(R.id.error_retry).also {
        it.setOnClickListener { retryCallback() }
    }

    fun bindTo(loadState: LoadState) {
        progressBar.isVisible = loadState is LoadState.Loading
        val error = loadState as? LoadState.Error
        retry.isVisible = error != null
        errorMsg.isVisible = error?.error?.message.isNullOrBlank()
        errorMsg.text = error?.error?.message
    }
}
