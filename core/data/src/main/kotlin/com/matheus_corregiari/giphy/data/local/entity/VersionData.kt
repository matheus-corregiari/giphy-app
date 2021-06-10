package com.matheus_corregiari.giphy.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "versionData")
internal class VersionData(
    @PrimaryKey @ColumnInfo(name = "tableName") val tableName: String,
    @ColumnInfo(name = "version") val version: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") var updatedAt: Long
)


