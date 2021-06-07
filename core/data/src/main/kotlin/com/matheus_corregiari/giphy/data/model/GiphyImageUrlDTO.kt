package com.matheus_corregiari.giphy.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GiphyImageUrlDTO(
    @Json(name = "url") val url: String
)