package com.matheus_corregiari.giphy.data.local

import android.content.Context
import androidx.room.Room

internal lateinit var database: AppDatabase
    private set

internal fun setupDatabase(context: Context) {
    database = Room.databaseBuilder(context, AppDatabase::class.java, "database-name").build()
}