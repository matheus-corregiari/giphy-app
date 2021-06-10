package com.matheus_corregiari.giphy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.matheus_corregiari.giphy.data.local.entity.Favorite
import com.matheus_corregiari.giphy.data.local.entity.VersionData

@Dao
internal interface VersionDataDao {
    @Query("SELECT * FROM versionData WHERE tableName = :tableName")
    suspend fun get(tableName: String): VersionData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: VersionData)

    @Delete
    suspend fun delete(item: VersionData)
}