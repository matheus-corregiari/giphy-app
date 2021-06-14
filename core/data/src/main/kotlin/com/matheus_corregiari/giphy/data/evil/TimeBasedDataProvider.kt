package com.matheus_corregiari.giphy.data.evil

import com.matheus_corregiari.giphy.data.local.storage.DatabaseProvider
import com.matheus_corregiari.giphy.data.local.storage.entity.VersionData
import com.matheus_corregiari.giphy.data.local.storage.entity.VersionDataStrategy
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

internal abstract class TimeBasedDataProvider<T> : DataRequestProvider<T>() {

    private val indexTimeToSkip = 1
    private val indexTimeToInvalidate = 2

    @ExperimentalTime
    open val timeToSkip: Long = Duration.seconds(30).inWholeMilliseconds

    @ExperimentalTime
    open val timeToInvalidate: Long = Duration.seconds(60).inWholeMilliseconds

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

    override suspend fun dumpLocal(version: VersionData) {
        DatabaseProvider.versionDataDao.delete(version)
    }

    override suspend fun isLocalStillValid(
        localVersion: VersionData,
        remoteVersion: VersionData?
    ): Boolean {
        return validateTime(localVersion, remoteVersion, indexTimeToSkip)
    }

    override suspend fun isLocalDisplayable(
        localVersion: VersionData,
        remoteVersion: VersionData?
    ): Boolean {
        return validateTime(localVersion, remoteVersion, indexTimeToInvalidate)
    }

    private fun validateTime(
        localVersion: VersionData,
        remoteVersion: VersionData?,
        intervalIndex: Int
    ): Boolean {
        val localVersionSplit = localVersion.version.split("|")
        val localDataInTimeMillis = localVersionSplit.first().toLong()
        val localDelayInTimeMillis = localVersionSplit[intervalIndex].toLong()
        val localDataExpire = localDataInTimeMillis + localDelayInTimeMillis
        val remoteDataInTimeMillis =
            (remoteVersion?.version ?: "0").split("|").first().toLong()
        return remoteDataInTimeMillis <= localDataExpire
    }
}