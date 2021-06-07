package com.matheus_corregiari.giphy.data.remote

import com.matheus_corregiari.giphy.data.model.GiphyResponseDTO
import retrofit2.http.GET
import retrofit2.http.Query

internal interface GiphyApi {
    @GET("trending")
    suspend fun fetchTrendingGifs(
        @Query("limit") gifLimit: Int,
        @Query("offset") offset: Int
    ): GiphyResponseDTO

    @GET("search")
    suspend fun searchGifs(
        @Query("limit") gifLimit: Int,
        @Query("offset") offset: Int,
        @Query("q") searchText: String
    ): GiphyResponseDTO
}

