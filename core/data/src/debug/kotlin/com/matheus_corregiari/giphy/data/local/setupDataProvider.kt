package com.matheus_corregiari.giphy.data.local

import androidx.room.RoomDatabase

internal fun <T : RoomDatabase> RoomDatabase.Builder<T>.setupOpenHelperFactory() = apply {  }