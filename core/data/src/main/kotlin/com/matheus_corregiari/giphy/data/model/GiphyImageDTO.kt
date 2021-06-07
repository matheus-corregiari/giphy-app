package com.matheus_corregiari.giphy.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GiphyImageDTO(
    @Json(name = "downsized") val downsized: GiphyImageUrlDTO
)