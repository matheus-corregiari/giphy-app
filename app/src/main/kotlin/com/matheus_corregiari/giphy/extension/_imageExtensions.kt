package com.matheus_corregiari.giphy.extension

import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.matheus_corregiari.giphy.R

fun AppCompatImageView.loadGifUrl(url: String) {
    val radius = resources.getDimensionPixelSize(R.dimen.giphy_item_border_radius)
    Glide.with(context)
        .load(url)
        .placeholder(R.drawable.bg_giphy_item_image_placeholder)
        .transition(DrawableTransitionOptions.withCrossFade())
        .transform(CenterCrop(), RoundedCorners(radius))
        .into(this)
}

fun AppCompatImageView.animateFavorite(favorite: Boolean, onEnd: () -> Unit) {
    val oldIcon = if (favorite) R.drawable.ic_round_unfavorite else R.drawable.ic_round_favorite
    val icon = if (favorite) R.drawable.ic_round_favorite else R.drawable.ic_round_unfavorite

    setImageResource(oldIcon)
    animateHide({ setImageResource(icon) }, onEnd)
}

private fun View.animateHide(endHideAction: () -> Unit, endAction: () -> Unit) {
    scaleX = 1f
    scaleY = 1f
    alpha = 1f
    animate()
        .setDuration(200L)
        .setInterpolator(OvershootInterpolator())
        .scaleX(0f).scaleY(0f).alpha(0.5f)
        .withEndAction {
            endHideAction.invoke()
            animateShow(endAction)
        }
}

private fun View.animateShow(endAction: () -> Unit) {
    scaleX = 0f
    scaleY = 0f
    alpha = 0.5f
    animate()
        .setDuration(400L)
        .setInterpolator(OvershootInterpolator())
        .scaleX(1f).scaleY(1f).alpha(1f)
        .withEndAction(endAction)
}