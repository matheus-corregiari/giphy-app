package com.matheus_corregiari.giphy.data.remote.interceptor

import com.matheus_corregiari.giphy.data.exception.BadRequestException
import com.matheus_corregiari.giphy.data.exception.ForbiddenException
import com.matheus_corregiari.giphy.data.exception.NotFoundException
import com.matheus_corregiari.giphy.data.exception.SdkException
import com.matheus_corregiari.giphy.data.exception.ServerException
import com.matheus_corregiari.giphy.data.exception.UnauthorizedException
import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Response

internal class ResponseInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val code = response.code

        if (response.isSuccessful) return response
        val path = request.url.encodedPath
        throw when (code) {
            500 -> ServerException("Server Exception", code, path)
            404 -> NotFoundException("Not Found Exception", code, path)
            403 -> ForbiddenException("Forbidden Exception", code, path)
            401 -> UnauthorizedException("Unauthorized Exception", code, path)
            400 -> BadRequestException("Bad Request Exception", code, path)
            else -> SdkException("Unmapped Http Exception", code, path)
        }
    }
}
