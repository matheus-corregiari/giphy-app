package com.matheus_corregiari.giphy.test

interface Given<WHEN : When<THEN>, THEN : Then> {
    fun whenCreator(): WHEN
    infix fun `when`(func: WHEN.() -> Unit) = whenCreator().apply(func)
    infix fun then(func: THEN.() -> Unit) = `when` {  }.thenCreator().apply(func)
}

interface When<THEN : Then> {
    fun thenCreator(): THEN
    infix fun then(func: THEN.() -> Unit) = thenCreator().apply(func)
}

interface Then