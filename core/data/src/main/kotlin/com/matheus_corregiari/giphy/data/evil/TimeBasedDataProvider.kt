package com.matheus_corregiari.giphy.data.evil

import com.matheus_corregiari.giphy.data.local.DatabaseProvider
import com.matheus_corregiari.giphy.data.local.entity.VersionData
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

internal abstract class TimeBasedDataProvider<T> : DataRequestProvider<T>() {

    abstract val tableName: String

    @ExperimentalTime
    override suspend fun remoteVersion(): VersionData? {
        val timeInMillis = System.currentTimeMillis()
        val version = "${timeInMillis}|${Duration.minutes(3).inWholeMilliseconds}"
        return VersionData(tableName, version, timeInMillis, timeInMillis)
    }

    override suspend fun localVersion(): VersionData? {
        return DatabaseProvider.versionDataDao.get(tableName)
    }

    override suspend fun saveLocal(version: VersionData, data: T) {
        version.updatedAt = System.currentTimeMillis()
        DatabaseProvider.versionDataDao.insert(version)
    }

    override suspend fun dump(version: VersionData) {
        DatabaseProvider.versionDataDao.delete(version)
    }

    override suspend fun isLocalDataDisplayable(
        localVersion: VersionData,
        remoteVersion: VersionData?
    ): Boolean {
        val localDataInTimeMillis = localVersion.version.split("|").first().toLong()
        val localDelayInTimeMillis = localVersion.version.split("|").last().toLong()
        val localDataExpire = localDataInTimeMillis + localDelayInTimeMillis
        val remoteDataInTimeMillis =
            remoteVersion?.version?.split("|")?.firstOrNull()?.toLongOrNull()
        return remoteDataInTimeMillis != null && remoteDataInTimeMillis <= localDataExpire
    }
}

