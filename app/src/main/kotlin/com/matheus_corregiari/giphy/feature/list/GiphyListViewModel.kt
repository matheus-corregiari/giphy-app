package com.matheus_corregiari.giphy.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import br.com.arch.toolkit.livedata.extention.mapList
import com.matheus_corregiari.giphy.data.RepositoryProvider
import com.matheus_corregiari.giphy.feature.model.GiphyItemDO
import kotlinx.coroutines.flow.map

class GiphyListViewModel : ViewModel() {

    private val repository = RepositoryProvider.giphyRepository

    fun testeLive() = repository.testeLiveData().mapList(::GiphyItemDO)
    fun testeFlow() = repository.testeFlow().map { it.map(::GiphyItemDO) }

    fun fetchList(onlyFavored: Boolean) = repository.trendingGiphys(onlyFavored).flow
        .cachedIn(viewModelScope)
        .map { it.map(::GiphyItemDO) }

    fun searchTerm(term: String?) = repository.searchGiphys(term).flow
        .cachedIn(viewModelScope)
        .map { it.map(::GiphyItemDO) }

    fun favorite(itemDO: GiphyItemDO) =
        repository.favoriteGiphy(itemDO.original)
}