package com.matheus_corregiari.giphy.data.evil

import com.matheus_corregiari.giphy.data.local.storage.DatabaseProvider
import com.matheus_corregiari.giphy.data.local.storage.entity.VersionData
import com.matheus_corregiari.giphy.data.local.storage.entity.VersionDataStrategy
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

internal abstract class TimeBasedDataProvider<T> : DataRequestProvider<T>() {

    abstract val tableName: String

    @ExperimentalTime
    open val timeToSkip: Long = Duration.seconds(0).inWholeMilliseconds

    @ExperimentalTime
    open val timeToInvalidate: Long = Duration.hours(200000).inWholeMilliseconds

    @ExperimentalTime
    override suspend fun remoteVersion(): VersionData? {
        val timeInMillis = System.currentTimeMillis()
        val version = "${timeInMillis}|${timeToSkip}|${timeToInvalidate}"
        return VersionData(
            tableName = tableName,
            version = version,
            createdAt = timeInMillis,
            updatedAt = timeInMillis,
            strategy = VersionDataStrategy.TIME_BASED
        )
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

    override suspend fun isVersionsTheSame(
        localVersion: VersionData,
        remoteVersion: VersionData?
    ): Boolean {
        if (localVersion.strategy != remoteVersion?.strategy) return false

        val localVersionSplit = localVersion.version.split("|")
        val localDataInTimeMillis = localVersionSplit.first().toLong()
        val localDelayInTimeMillis = localVersionSplit[1].toLong()
        val localDataExpire = localDataInTimeMillis + localDelayInTimeMillis
        val remoteDataInTimeMillis =
            remoteVersion.version.split("|").first().toLong()
        return remoteDataInTimeMillis <= localDataExpire
    }

    override suspend fun isLocalDataDisplayable(
        localVersion: VersionData,
        remoteVersion: VersionData?
    ): Boolean {
        if (localVersion.strategy != remoteVersion?.strategy) return false

        val localVersionSplit = localVersion.version.split("|")
        val localDataInTimeMillis = localVersionSplit.first().toLong()
        val localDelayInTimeMillis = localVersionSplit.last().toLong()
        val localDataExpire = localDataInTimeMillis + localDelayInTimeMillis
        val remoteDataInTimeMillis =
            remoteVersion.version.split("|").first().toLong()
        return remoteDataInTimeMillis <= localDataExpire
    }
}