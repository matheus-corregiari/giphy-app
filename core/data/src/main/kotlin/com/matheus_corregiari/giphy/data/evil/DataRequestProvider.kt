package com.matheus_corregiari.giphy.data.evil

import android.util.Log
import br.com.arch.toolkit.livedata.response.DataResult
import br.com.arch.toolkit.livedata.response.DataResultStatus
import br.com.arch.toolkit.livedata.response.ResponseLiveData
import com.matheus_corregiari.giphy.data.evil.livedata.responseLiveData
import com.matheus_corregiari.giphy.data.evil.version.VersionStrategy
import com.matheus_corregiari.giphy.data.exception.MultipleErrorsException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

internal abstract class DataRequestProvider<T> {

    abstract val versionStrategy: VersionStrategy
    abstract val strategy: Strategy
    abstract val recurrence: RecurrenceStrategy

    abstract suspend fun loadRemote(): T
    abstract suspend fun loadLocal(): T
    abstract suspend fun saveLocal(data: T)
    abstract suspend fun dumpLocal()

    open val dataFlow: Flow<DataResult<T>> = buildFlow(strategy).flowOn(Dispatchers.IO)
    open val versionFlow: Flow<DataResult<Long>> = flow<DataResult<Long>> {
        emit(loadingResult())
        when (recurrence) {
            RecurrenceStrategy.ONE_SHOT -> {
                kotlin.runCatching { versionStrategy.local()?.updatedAt }
                    .onSuccess { emit(successResult(it)) }
                    .onFailure { emit(errorResult(errors = listOf(it))) }
            }
            RecurrenceStrategy.OBSERVABLE -> TODO()
        }
    }.flowOn(Dispatchers.IO)

    open val dataLiveData: ResponseLiveData<T>
        get() = dataFlow.responseLiveData
    open val versionLiveData: ResponseLiveData<Long>
        get() = versionFlow.responseLiveData

    private fun buildFlow(strategy: Strategy) = flow {
        emit(loadingResult())
        kotlin.runCatching {
            when (strategy) {
                Strategy.REQUEST -> when (recurrence) {
                    RecurrenceStrategy.ONE_SHOT -> emitAll(remoteFlow())
                    RecurrenceStrategy.OBSERVABLE -> {
                        val errors =
                            listOf(IllegalArgumentException("Request strategy cannot be observable"))
                        emit(errorResult<T>(errors = errors))
                    }
                }

                Strategy.LOCAL -> when (recurrence) {
                    RecurrenceStrategy.ONE_SHOT -> emitAll(localFlow())
                    RecurrenceStrategy.OBSERVABLE -> TODO()
                }

                Strategy.AUTO -> when (recurrence) {
                    RecurrenceStrategy.ONE_SHOT -> emitAll(autoFlow())
                    RecurrenceStrategy.OBSERVABLE -> TODO()
                }
            }
        }
    }

    private fun remoteFlow(
        localData: T? = null,
        flowErrors: MutableList<Throwable> = mutableListOf()
    ) = flow<DataResult<T>> {
        Log.wtf("REQUEST PROVIDER", "START REMOTE FLOW")
        kotlin.runCatching { loadRemote() }
            .onFailure(flowErrors::add)
            .onFailure { emit(errorResult(data = localData, errors = flowErrors)) }
            .onSuccess { result ->
                emit(successResult(data = result))
                internalSaveLocal(result)
            }
    }

    private fun localFlow(flowErrors: MutableList<Throwable> = mutableListOf()) =
        flow<DataResult<T>> {
            Log.wtf("REQUEST PROVIDER", "START LOCAL FLOW")
            kotlin.runCatching {
                val localVersion = versionStrategy.local()
                    ?: throw IllegalStateException("Cannot retrieve local version")
                val removeVersion = versionStrategy.remote()
                if (versionStrategy.isLocalDisplayable(localVersion, removeVersion)) {
                    emit(successResult(data = loadLocal()))
                    versionStrategy.saveVersion(localVersion)
                } else {
                    internalRemoveLocal()
                }
            }.onFailure(flowErrors::add).onFailure { emit(errorResult(errors = flowErrors)) }
        }

    private fun autoFlow() = flow<DataResult<T>> {
        val flowErrors = mutableListOf<Throwable>()
        kotlin.runCatching {
            Log.wtf("REQUEST PROVIDER", "START AUTO FLOW")
            versionStrategy.local()?.let { localVersion ->
                val removeVersion = versionStrategy.remote()
                if (versionStrategy.isLocalDisplayable(localVersion, removeVersion)) {
                    kotlin.runCatching { loadLocal() }
                        .onSuccess { data ->
                            val isVersionValid =
                                versionStrategy.isLocalStillValid(localVersion, removeVersion)
                            if (isVersionValid) {
                                emit(successResult(data, flowErrors))
                                versionStrategy.saveVersion(localVersion)
                            } else {
                                emit(loadingResult(data, flowErrors))
                                emitAll(remoteFlow(data, flowErrors))
                            }
                        }
                        .onFailure(flowErrors::add)
                        .onFailure { emitAll(remoteFlow(flowErrors = flowErrors)) }
                } else {
                    internalRemoveLocal()
                    emitAll(remoteFlow(flowErrors = flowErrors))
                }
            } ?: emitAll(remoteFlow(flowErrors = flowErrors))
        }.onFailure(flowErrors::add).onFailure { emit(errorResult(errors = flowErrors)) }
    }

    private suspend fun internalSaveLocal(data: T) {
        val remoteVersion = versionStrategy.remote() ?: return
        kotlin.runCatching {
            versionStrategy.saveVersion(remoteVersion)
            saveLocal(data)
        }.onFailure { versionStrategy.removeVersion(remoteVersion) }
    }

    private suspend fun internalRemoveLocal() {
        val localVersion = versionStrategy.local() ?: return
        kotlin.runCatching {
            versionStrategy.removeVersion(localVersion)
            dumpLocal()
        }
    }

    private fun <R> errorResult(data: R? = null, errors: List<Throwable>? = null) = DataResult(
        data = data, error = errors?.let(::MultipleErrorsException), status = DataResultStatus.ERROR
    )

    private fun <R> successResult(data: R? = null, errors: List<Throwable>? = null) = DataResult(
        data = data,
        error = errors?.let(::MultipleErrorsException),
        status = DataResultStatus.SUCCESS
    )

    private fun <R> loadingResult(data: R? = null, errors: List<Throwable>? = null) = DataResult(
        data = data,
        error = errors?.let(::MultipleErrorsException),
        status = DataResultStatus.LOADING
    )

    enum class Strategy {
        REQUEST, LOCAL, AUTO
    }

    enum class RecurrenceStrategy {
        ONE_SHOT, OBSERVABLE
    }
}