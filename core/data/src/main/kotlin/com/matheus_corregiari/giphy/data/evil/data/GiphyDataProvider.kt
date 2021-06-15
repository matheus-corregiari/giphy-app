package com.matheus_corregiari.giphy.data.evil.data

import br.com.arch.toolkit.livedata.response.DataResult
import com.matheus_corregiari.giphy.data.evil.DataRequestProvider
import com.matheus_corregiari.giphy.data.evil.version.TimeBasedVersionStrategy
import com.matheus_corregiari.giphy.data.evil.version.VersionStrategy
import com.matheus_corregiari.giphy.data.local.storage.DatabaseProvider
import com.matheus_corregiari.giphy.data.local.storage.entity.Giphy
import com.matheus_corregiari.giphy.data.model.GiphyItemDTO
import com.matheus_corregiari.giphy.data.remote.ApiProvider
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal class GiphyDataProvider :
    DataRequestProvider<List<GiphyItemDTO>>(Strategy.AUTO, RecurrenceStrategy.ONE_SHOT) {

    @ExperimentalTime
    override val versionStrategy: VersionStrategy = TimeBasedVersionStrategy(tableName = "giphy")

    override val dataFlow: Flow<DataResult<List<GiphyItemDTO>>>
        get() = super.dataFlow.combine(
            DatabaseProvider.favoriteDao.getAllFavoriteId(),
            ::combineGiphyWithFavorites
        )

    override suspend fun loadRemote(): List<GiphyItemDTO> {
        return ApiProvider.api.fetchTrendingGifs(10, 1).gifList
    }

    override suspend fun loadLocal(): List<GiphyItemDTO> {
        return DatabaseProvider.giphyDao.getAll().map(Giphy::asGiphyItemDTO)
    }

    override suspend fun saveLocal(data: List<GiphyItemDTO>) {
        DatabaseProvider.giphyDao.dump()
        DatabaseProvider.giphyDao.insertAll(data.map(GiphyItemDTO::asGiphy))
    }

    override suspend fun dumpLocal() {
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