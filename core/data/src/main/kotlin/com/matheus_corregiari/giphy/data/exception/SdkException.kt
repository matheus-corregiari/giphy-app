package com.matheus_corregiari.giphy.data.exception

open class SdkException internal constructor(
    message: String,
    val code: Int,
    val requestedPath: String
) : Exception(message)
