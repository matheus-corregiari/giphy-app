package com.matheus_corregiari.giphy.data.remote.interceptor

import com.matheus_corregiari.giphy.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

internal class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url
        val hasNotApiQueryParam = url.queryParameter("api_key").isNullOrBlank()

        return if (hasNotApiQueryParam) {
            val newUrl = url.newBuilder().addQueryParameter("api_key", BuildConfig.API_KEY).build()
            val newRequest = request.newBuilder().url(newUrl).build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(request)
        }
    }
}