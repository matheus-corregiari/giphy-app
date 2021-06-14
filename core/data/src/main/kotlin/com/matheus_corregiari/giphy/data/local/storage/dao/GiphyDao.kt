package com.matheus_corregiari.giphy.data.local.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.matheus_corregiari.giphy.data.local.storage.entity.Giphy
import kotlinx.coroutines.flow.Flow

@Dao
internal interface GiphyDao {
    @Query("SELECT * FROM giphy")
    suspend fun getAll(): List<Giphy>

    @Query("SELECT * FROM giphy")
    fun getAllFlow(): Flow<List<Giphy>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Giphy>)

    @Query("DELETE FROM giphy")
    suspend fun dump()
}