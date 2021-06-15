package com.matheus_corregiari.giphy.data.evil.version

import com.matheus_corregiari.giphy.data.local.storage.DatabaseProvider
import com.matheus_corregiari.giphy.data.local.storage.entity.VersionData
import com.matheus_corregiari.giphy.data.local.storage.entity.VersionDataStrategy
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal open class TimeBasedVersionStrategy(
    override val tableName: String,
    timeToSkip: Duration = Duration.minutes(1),
    timeToInvalidate: Duration = Duration.minutes(30)
) : VersionStrategy() {

    private val indexTimeToSkip = 1
    private val indexTimeToInvalidate = 2

    private val timeToSkipInMillis: Long = timeToSkip.inWholeMilliseconds
    private val timeToInvalidateInMillis: Long = timeToInvalidate.inWholeMilliseconds

    override suspend fun remote(): VersionData? {
        return kotlin.runCatching {
            val timeInMillis = System.currentTimeMillis()
            val version = "${timeInMillis}|${timeToSkipInMillis}|${timeToInvalidateInMillis}"
            VersionData(
                tableName = tableName,
                version = version,
                createdAt = timeInMillis,
                updatedAt = timeInMillis,
                strategy = VersionDataStrategy.TIME_BASED
            )
        }.getOrNull()
    }

    override suspend fun local(): VersionData? {
        return kotlin.runCatching { DatabaseProvider.versionDataDao.get(tableName) }.getOrNull()
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

    override suspend fun saveVersion(version: VersionData) {
        kotlin.runCatching {
            val localVersion = local() ?: version
            localVersion.updatedAt = version.updatedAt
            DatabaseProvider.versionDataDao.insert(version)
        }
    }

    override suspend fun removeVersion(version: VersionData) {
        kotlin.runCatching { DatabaseProvider.versionDataDao.delete(version) }
    }

    private fun validateTime(
        localVersion: VersionData,
        remoteVersion: VersionData?,
        intervalIndex: Int
    ): Boolean {
        return kotlin.runCatching {
            val localVersionSplit = localVersion.version.split("|")
            val localDataInTimeMillis = localVersionSplit.first().toLong()
            val localDelayInTimeMillis = localVersionSplit[intervalIndex].toLong()
            val localDataExpire = localDataInTimeMillis + localDelayInTimeMillis
            val remoteDataInTimeMillis =
                (remoteVersion?.version ?: "0").split("|").first().toLong()
            remoteDataInTimeMillis <= localDataExpire
        }.getOrDefault(false)
    }
}