package com.matheus_corregiari.giphy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.matheus_corregiari.giphy.data.local.entity.Giphy

@Dao
internal interface GiphyDao {
    @Query("SELECT * FROM giphy")
    suspend fun getAll(): List<Giphy>

    @Query("SELECT * FROM giphy WHERE id = :id")
    suspend fun get(id: String): Giphy?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Giphy)

    @Delete
    suspend fun delete(item: Giphy)

    @Query("DELETE FROM giphy")
    suspend fun dump()
}