package com.matheus_corregiari.giphy.data

import com.matheus_corregiari.giphy.data.repository.GiphyRepository

object RepositoryProvider {

    val giphyRepository by lazy(::GiphyRepository)

}