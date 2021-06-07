package com.matheus_corregiari.giphy.data.exception

class UnauthorizedException internal constructor(
    message: String,
    code: Int,
    requestedPath: String
) : SdkException(message, code, requestedPath)
