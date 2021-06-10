package com.matheus_corregiari.giphy.data.evil.data

import com.matheus_corregiari.giphy.data.evil.TimeBasedDataProvider
import com.matheus_corregiari.giphy.data.local.DatabaseProvider
import com.matheus_corregiari.giphy.data.local.entity.VersionData
import com.matheus_corregiari.giphy.data.model.GiphyItemDTO

internal class GiphyDataProvider : TimeBasedDataProvider<List<GiphyItemDTO>>() {

    override val tableName: String = "giphy"

    override suspend fun remoteLoad(): List<GiphyItemDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun localLoad(): List<GiphyItemDTO> {
        TODO("Not yet implemented")

    }

    override suspend fun saveLocal(version: VersionData, data: List<GiphyItemDTO>) {
        super.saveLocal(version, data)
    }

    override suspend fun dump(version: VersionData) {
        super.dump(version)
        DatabaseProvider.giphyDao.dump()

    }
}
