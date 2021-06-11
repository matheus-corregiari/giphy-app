package com.matheus_corregiari.giphy.data.exception

class MultipleErrorsException internal constructor(val errors: List<Throwable>) :
    SdkException("Multiple errors", 666, "Multiple errors")
