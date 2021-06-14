package com.matheus_corregiari.giphy.data.evil.data

import com.matheus_corregiari.giphy.data.evil.TimeBasedDataProvider
import com.matheus_corregiari.giphy.data.local.storage.DatabaseProvider
import com.matheus_corregiari.giphy.data.local.storage.entity.Giphy
import com.matheus_corregiari.giphy.data.local.storage.entity.VersionData
import com.matheus_corregiari.giphy.data.model.GiphyItemDTO
import com.matheus_corregiari.giphy.data.remote.ApiProvider

internal class GiphyDataProvider : TimeBasedDataProvider<List<GiphyItemDTO>>() {

    override val tableName: String = "giphy"

    override suspend fun loadRemote(): List<GiphyItemDTO> {
        return ApiProvider.api.fetchTrendingGifs(10, 1).gifList
    }

    override suspend fun loadLocal(): List<GiphyItemDTO> {
        return DatabaseProvider.giphyDao.getAll().map(Giphy::asGiphyItemDTO)
            .onEach { it.favorite = isFavorite(it.id) }
    }

    override suspend fun saveLocal(version: VersionData, data: List<GiphyItemDTO>) {
        super.saveLocal(version, data)
        DatabaseProvider.giphyDao.dump()
        DatabaseProvider.giphyDao.insertAll(data.map(GiphyItemDTO::asGiphy))
    }

    override suspend fun dumpLocal(version: VersionData) {
        super.dumpLocal(version)
        DatabaseProvider.giphyDao.dump()
    }

    private suspend fun isFavorite(id: String): Boolean {
        return kotlin.runCatching { DatabaseProvider.favoriteDao.get(id) != null }
            .getOrElse { false }
    }
}