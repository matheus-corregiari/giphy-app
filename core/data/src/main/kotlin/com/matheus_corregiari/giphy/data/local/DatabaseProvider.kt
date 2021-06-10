package com.matheus_corregiari.giphy.data.local

import android.content.Context
import androidx.room.Room
import com.matheus_corregiari.giphy.data.local.dao.FavoriteDao
import com.matheus_corregiari.giphy.data.local.dao.GiphyDao
import com.matheus_corregiari.giphy.data.local.dao.VersionDataDao

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