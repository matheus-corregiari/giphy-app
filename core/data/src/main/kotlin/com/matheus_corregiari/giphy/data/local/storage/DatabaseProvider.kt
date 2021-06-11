package com.matheus_corregiari.giphy.data.local.storage

import android.content.Context
import androidx.room.Room
import com.matheus_corregiari.giphy.data.local.storage.dao.FavoriteDao
import com.matheus_corregiari.giphy.data.local.storage.dao.GiphyDao
import com.matheus_corregiari.giphy.data.local.storage.dao.VersionDataDao
import com.matheus_corregiari.giphy.data.local.setupOpenHelperFactory

internal object DatabaseProvider {
    private lateinit var database: AppDatabase

    val favoriteDao: FavoriteDao
        get() = database.favorite()

    val versionDataDao: VersionDataDao
        get() = database.versionDataDao()

    val giphyDao: GiphyDao
        get() = database.giphyDao()

    internal fun setupDatabase(context: Context) {
        database = Room.databaseBuilder(context, AppDatabase::class.java, "giphy-app.db")
            .setupOpenHelperFactory()
            .build()
    }
}