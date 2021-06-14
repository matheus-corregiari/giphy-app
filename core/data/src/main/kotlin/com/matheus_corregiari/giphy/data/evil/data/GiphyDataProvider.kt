package com.matheus_corregiari.giphy.data.evil.data

import br.com.arch.toolkit.livedata.response.DataResult
import com.matheus_corregiari.giphy.data.evil.TimeBasedDataProvider
import com.matheus_corregiari.giphy.data.local.storage.DatabaseProvider
import com.matheus_corregiari.giphy.data.local.storage.entity.Giphy
import com.matheus_corregiari.giphy.data.local.storage.entity.VersionData
import com.matheus_corregiari.giphy.data.model.GiphyItemDTO
import com.matheus_corregiari.giphy.data.remote.ApiProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

internal class GiphyDataProvider : TimeBasedDataProvider<List<GiphyItemDTO>>() {

    override val tableName: String = "giphy"

    override val flow: Flow<DataResult<List<GiphyItemDTO>>>
        get() = super.flow.combine(
            DatabaseProvider.favoriteDao.getAllFavoriteId(),
            ::combineGiphyWithFavorites
        )

    override suspend fun loadRemote(): List<GiphyItemDTO> {
        return ApiProvider.api.fetchTrendingGifs(10, 1).gifList
    }

    override suspend fun loadLocal(): List<GiphyItemDTO> {
        return DatabaseProvider.giphyDao.getAll().map(Giphy::asGiphyItemDTO)
    }

    override suspend fun loadLocalFlow(): Flow<List<GiphyItemDTO>> {
        return DatabaseProvider.giphyDao.getAllFlow().map { it.map(Giphy::asGiphyItemDTO) }
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

    private fun combineGiphyWithFavorites(
        result: DataResult<List<GiphyItemDTO>>, favoriteIdList: List<String>
    ): DataResult<List<GiphyItemDTO>> {
        val giphyList = result.data
        giphyList?.onEach { giphy -> giphy.favorite = favoriteIdList.contains(giphy.id) }
        return DataResult(giphyList, result.error, result.status)
    }
}