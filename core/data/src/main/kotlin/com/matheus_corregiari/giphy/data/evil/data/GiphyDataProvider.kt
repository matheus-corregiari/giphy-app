package com.matheus_corregiari.giphy.data.evil.data

import com.matheus_corregiari.giphy.data.evil.TimeBasedDataProvider
import com.matheus_corregiari.giphy.data.local.storage.DatabaseProvider
import com.matheus_corregiari.giphy.data.local.storage.entity.Giphy
import com.matheus_corregiari.giphy.data.local.storage.entity.VersionData
import com.matheus_corregiari.giphy.data.model.GiphyItemDTO
import com.matheus_corregiari.giphy.data.remote.ApiProvider

internal class GiphyDataProvider : TimeBasedDataProvider<List<GiphyItemDTO>>() {

    override val tableName: String = "giphy"

    override suspend fun remoteLoad(): List<GiphyItemDTO> {
        return ApiProvider.api.fetchTrendingGifs(10, 1).gifList
    }

    override suspend fun localLoad(): List<GiphyItemDTO> {
        return DatabaseProvider.giphyDao.getAll().map(Giphy::asGiphyItemDTO)
    }

    override suspend fun saveLocal(version: VersionData, data: List<GiphyItemDTO>) {
        super.saveLocal(version, data)
        DatabaseProvider.giphyDao.dump()
        DatabaseProvider.giphyDao.insertAll(data.map(GiphyItemDTO::asGiphy))
    }

    override suspend fun dump(version: VersionData) {
        super.dump(version)
        DatabaseProvider.giphyDao.dump()
    }
}

// App --T--> Apigee --T--> Serviço
// App <--T-- Apigee <--T-- Serviço


// App --T--> Apigee --T--> NGinx --T--> Serviço
// App <--T-- Apigee <--T-- NGinx <--T-- Serviço

// App --T--> Apigee --T--> NGinx --X-- Serviço
// App <--T-- Apigee <--T-- NGinx


// usuario navegando
// push silencioso { UPDATE BALANCE PERSON_ID } -> salva numa tabela auxiliar e varre ela no momento do proximo login
// push silencioso { DUMP STATEMENT }