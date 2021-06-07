package com.matheus_corregiari.giphy.data.model

import com.matheus_corregiari.giphy.data.local.entity.Favorite
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GiphyItemDTO(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "images") val images: GiphyImageDTO
) {
    var favorite: Boolean = false

    internal fun asFavorite() = Favorite(id, title, images.downsized.url)
}


