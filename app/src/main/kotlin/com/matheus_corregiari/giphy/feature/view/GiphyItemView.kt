package com.matheus_corregiari.giphy.feature.view

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import br.com.arch.toolkit.delegate.viewProvider
import com.matheus_corregiari.giphy.R
import com.matheus_corregiari.giphy.extension.animateFavorite
import com.matheus_corregiari.giphy.extension.loadGifUrl
import com.matheus_corregiari.giphy.feature.model.GiphyItemDO

class GiphyItemView(context: Context) : ConstraintLayout(context) {

    //region Views
    private val image: AppCompatImageView by viewProvider(R.id.gif_view)
    private val title: AppCompatTextView by viewProvider(R.id.title_view)
    private val favoriteAction: AppCompatImageView by viewProvider(R.id.favorite_view)
    //endregion

    init {
        inflate(context, R.layout.view_giphy_item, this)
        setBackgroundResource(R.drawable.bg_giphy_item)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    fun bind(model: GiphyItemDO) {
        image.loadGifUrl(model.gifUrl)
        title.text = model.name
        title.isVisible = model.name.isNotBlank()

        val oldModel = favoriteAction.tag as? GiphyItemDO
        val oldId = oldModel?.id
        if (oldId == model.id && oldModel.favorite != model.favorite) {
            favoriteAction.animateFavorite(model.favorite)
        } else {
            favoriteAction.setImageResource(
                if (model.favorite) R.drawable.ic_round_favorite
                else R.drawable.ic_round_unfavorite
            )
        }
        favoriteAction.tag = model
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        favoriteAction.setOnClickListener {
            (favoriteAction.tag as? GiphyItemDO)?.let { model ->
                model.favorite = model.favorite.not()
                favoriteAction.animateFavorite(model.favorite)
                listener?.onClick(it)
            }
        }
    }
}