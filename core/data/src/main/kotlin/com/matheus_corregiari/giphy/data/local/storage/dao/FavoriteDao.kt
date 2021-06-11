package com.matheus_corregiari.giphy.data.local.storage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.matheus_corregiari.giphy.data.local.storage.entity.Favorite

@Dao
internal interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    suspend fun getAll(): List<Favorite>

    @Query("SELECT * FROM favorite WHERE id = :id")
    suspend fun get(id: String): Favorite?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Favorite)

    @Delete
    suspend fun delete(item: Favorite)
}