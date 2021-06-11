package com.matheus_corregiari.giphy.data.local.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.matheus_corregiari.giphy.data.local.storage.dao.FavoriteDao
import com.matheus_corregiari.giphy.data.local.storage.dao.GiphyDao
import com.matheus_corregiari.giphy.data.local.storage.dao.VersionDataDao
import com.matheus_corregiari.giphy.data.local.storage.entity.Favorite
import com.matheus_corregiari.giphy.data.local.storage.entity.Giphy
import com.matheus_corregiari.giphy.data.local.storage.entity.VersionData

@Database(entities = [Favorite::class, Giphy::class, VersionData::class], version = 2)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun favorite(): FavoriteDao
    abstract fun versionDataDao(): VersionDataDao
    abstract fun giphyDao(): GiphyDao
}
