package com.matheus_corregiari.giphy.data

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.matheus_corregiari.giphy.data.remote.ApiProvider
import okhttp3.logging.HttpLoggingInterceptor

class DebugDataInitialization : ContentProvider() {

    override fun onCreate(): Boolean {
        context?.let {
            val debugInterceptor = HttpLoggingInterceptor()
            debugInterceptor.level = HttpLoggingInterceptor.Level.BODY
            ApiProvider.setupApi(StethoInterceptor(), debugInterceptor)
            Stetho.initializeWithDefaults(it);
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0
}