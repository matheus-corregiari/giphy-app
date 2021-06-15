package com.matheus_corregiari.giphy.data.evil.version

import com.matheus_corregiari.giphy.data.local.storage.entity.VersionData

internal sealed class VersionStrategy {
    abstract val tableName: String

    abstract suspend fun remote(): VersionData?
    abstract suspend fun local(): VersionData?

    abstract suspend fun isLocalStillValid(
        localVersion: VersionData,
        remoteVersion: VersionData?
    ): Boolean

    abstract suspend fun isLocalDisplayable(
        localVersion: VersionData,
        remoteVersion: VersionData?
    ): Boolean

    abstract suspend fun saveVersion(version: VersionData)

    abstract suspend fun removeVersion(version: VersionData)
}
