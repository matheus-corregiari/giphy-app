package com.matheus_corregiari.giphy.test

import com.matheus_corregiari.giphy.data.RepositoryProvider
import io.mockk.mockkClass
import java.lang.reflect.Field
import kotlin.reflect.jvm.kotlinProperty
import net.vidageek.mirror.dsl.Mirror
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockRepositoriesRule : TestRule {
    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                mockRepositories()
                base?.evaluate()
            }
        }
    }

    private fun mockRepositories() {
        val fieldsToMock =
            RepositoryProvider::class.java.declaredFields.filter { it.type.name == Lazy::class.java.name }
        fieldsToMock.onEach(::mockRepositoryInstance)
    }

    private fun mockRepositoryInstance(field: Field) {
        val classToMock = Class.forName(field.kotlinProperty!!.returnType.toString())
        val mockedField = mockkClass(classToMock.kotlin, relaxed = true)
        Mirror().on(RepositoryProvider::class.java).set().field(field)
            .withValue(lazy { mockedField })
    }

}