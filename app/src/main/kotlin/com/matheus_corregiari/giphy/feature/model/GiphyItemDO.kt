package com.matheus_corregiari.giphy.feature.model

import com.matheus_corregiari.giphy.data.model.GiphyItemDTO

class GiphyItemDO(
    val id: String,
    val gifUrl: String,
    val name: String,
    var favorite: Boolean,
    val original: GiphyItemDTO
) {
    constructor(dtoItem: GiphyItemDTO) : this(
        id = dtoItem.id,
        gifUrl = dtoItem.images.downsized.url,
        name = dtoItem.title,
        favorite = dtoItem.favorite,
        original = dtoItem
    )
}