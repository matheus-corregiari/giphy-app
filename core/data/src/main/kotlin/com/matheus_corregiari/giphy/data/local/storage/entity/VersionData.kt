package com.matheus_corregiari.giphy.data.local.storage.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "versionData")
internal class VersionData(
    @PrimaryKey @ColumnInfo(name = "tableName") val tableName: String,
    @ColumnInfo(name = "version") val version: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") var updatedAt: Long,
    @ColumnInfo(name = "strategy") val strategy: VersionDataStrategy
)

internal enum class VersionDataStrategy {
    TIME_BASED, CALENDAR_BASED, REQUEST_BASED
}

