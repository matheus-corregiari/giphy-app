package com.matheus_corregiari.giphy.data.local

import androidx.room.RoomDatabase
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

internal fun <T : RoomDatabase> RoomDatabase.Builder<T>.setupOpenHelperFactory() =
    apply { openHelperFactory(SupportFactory(SQLiteDatabase.getBytes("GiphyAppPassphrase".toCharArray()))) }
