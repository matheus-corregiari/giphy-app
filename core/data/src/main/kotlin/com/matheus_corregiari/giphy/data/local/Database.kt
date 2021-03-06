package com.matheus_corregiari.giphy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.matheus_corregiari.giphy.data.local.dao.FavoriteDao
import com.matheus_corregiari.giphy.data.local.entity.Favorite

@Database(entities = [Favorite::class], version = 1)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun favorite(): FavoriteDao
}
