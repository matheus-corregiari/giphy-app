package com.matheus_corregiari.giphy.data.remote

import com.matheus_corregiari.giphy.data.BuildConfig
import com.matheus_corregiari.giphy.data.remote.interceptor.ApiKeyInterceptor
import com.matheus_corregiari.giphy.data.remote.interceptor.ResponseInterceptor
import com.squareup.moshi.Moshi
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal val api: GiphyApi by lazy { initRetrofit() }

private fun initRetrofit() = Retrofit.Builder()
    .baseUrl(BuildConfig.BASE_URL)
    .client(initOkHttp())
    .addConverterFactory(MoshiConverterFactory.create(initMoshi()))
    .build()
    .create(GiphyApi::class.java)

private fun initOkHttp() = OkHttpClient.Builder()
    .readTimeout(30L, TimeUnit.SECONDS)
    .connectTimeout(30L, TimeUnit.SECONDS)
    .callTimeout(30L, TimeUnit.SECONDS)
    .addInterceptor(ApiKeyInterceptor())
    .addInterceptor(ResponseInterceptor())
    .apply {
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(loggingInterceptor)
        }
    }
    .build()

private fun initMoshi() = Moshi.Builder().build()
