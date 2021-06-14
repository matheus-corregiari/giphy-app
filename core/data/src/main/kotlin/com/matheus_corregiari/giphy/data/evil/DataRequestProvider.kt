package com.matheus_corregiari.giphy.data.evil

import android.util.Log
import br.com.arch.toolkit.livedata.response.DataResult
import br.com.arch.toolkit.livedata.response.DataResultStatus
import br.com.arch.toolkit.livedata.response.ResponseLiveData
import com.matheus_corregiari.giphy.data.evil.livedata.ResultMutableResponseLiveData
import com.matheus_corregiari.giphy.data.exception.MultipleErrorsException
import com.matheus_corregiari.giphy.data.local.storage.entity.VersionData
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

internal abstract class DataRequestProvider<T> {

    abstract val tableName: String

    abstract suspend fun loadRemote(): T
    abstract suspend fun loadLocal(): T
    abstract suspend fun loadLocalFlow(): Flow<T>

    abstract suspend fun remoteVersion(): VersionData?
    abstract suspend fun localVersion(): VersionData?

    abstract suspend fun saveLocal(version: VersionData, data: T)
    abstract suspend fun dumpLocal(version: VersionData)

    abstract suspend fun isLocalStillValid(
        localVersion: VersionData,
        remoteVersion: VersionData?
    ): Boolean

    abstract suspend fun isLocalDisplayable(
        localVersion: VersionData,
        remoteVersion: VersionData?
    ): Boolean

    open val flow: Flow<DataResult<T>> = flow {
        emit(null.loadingResult(null))
        kotlin.runCatching { executeOperation() }
            .onFailure { emit(errorResult(listOf(it), null)) }
    }.flowOn(Dispatchers.IO)

    open val liveData: ResponseLiveData<T>
        get() {
            val liveData = ResultMutableResponseLiveData<T>()
            GlobalScope.launch {
                flow.collectLatest {
                    liveData.postValue(it)
                }
            }
            return liveData
        }

    private suspend fun FlowCollector<DataResult<T>>.executeOperation() {
        val flowErrors = mutableListOf<Throwable>()

        var needCallRemote = true
        var remoteVersionWithError = false
        var localData: T? = null
        val localVersion =
            kotlin.runCatching { localVersion() }.onFailure(flowErrors::add).getOrNull()
        val remoteVersion = kotlin.runCatching { remoteVersion() }
            .onSuccess { remoteVersionWithError = false }
            .onFailure { remoteVersionWithError = true }
            .onFailure(flowErrors::add)
            .getOrNull()

        if (localVersion != null) {
            if (localVersion.strategy != remoteVersion?.strategy) {
                dumpLocal(localVersion)
            } else {
                localData = kotlin.runCatching { loadLocal() }.onSuccess { data ->
                    needCallRemote = isLocalStillValid(localVersion, remoteVersion).not()
                    val isLocalDisplayable = isLocalDisplayable(
                        localVersion,
                        remoteVersion
                    )
                    if (remoteVersionWithError || isLocalDisplayable) {
                        if (needCallRemote) {
                            Log.wtf("TEST FLOW", "LOCAL DATA, BUT SHOULD REQUEST API")
                            emit(data.loadingResult(flowErrors))
                        } else {
                            Log.wtf("TEST FLOW", "DATA STILL VALID")
                            emit(data.successResult(flowErrors))
                            return
                        }
                    } else {
                        dumpLocal(localVersion)
                    }
                }.onFailure(flowErrors::add).getOrNull()
            }
        }
        if (needCallRemote) {
            requestApi(remoteVersion, localData, flowErrors)
        }
    }

    private suspend fun FlowCollector<DataResult<T>>.requestApi(
        remoteVersion: VersionData?,
        localData: T?,
        flowErrors: MutableList<Throwable>
    ) {
        Log.wtf("TEST FLOW", "REQUEST API")
        kotlin.runCatching { loadRemote() }.onSuccess { newData ->
            if (remoteVersion != null) {
                kotlin.runCatching { saveLocal(remoteVersion, newData) }
                    .onFailure(flowErrors::add)
            }
            emit(newData.successResult(flowErrors))
        }.onFailure(flowErrors::add)
            .onFailure { emit(errorResult(flowErrors, localData)) }
    }

    private fun errorResult(list: List<Throwable>, data: T?) = DataResult(
        data = data, error = MultipleErrorsException(list), status = DataResultStatus.ERROR
    )

    private fun T?.successResult(list: List<Throwable>) = DataResult(
        data = this, error = MultipleErrorsException(list), status = DataResultStatus.SUCCESS
    )

    private fun T?.loadingResult(list: List<Throwable>?) = DataResult(
        data = this, error = list?.let(::MultipleErrorsException), status = DataResultStatus.LOADING
    )


    @FlowPreview
    private suspend fun testReactive() {

        // flow dados
        // tooodo dado local (quando updatado)


        val remoteFlow = flow<DataResult<T>> {
            emit(null.loadingResult(null))
            val remoteVersion: VersionData? = kotlin.runCatching { remoteVersion() }.getOrNull()
            kotlin.runCatching {
                loadRemote()
            }.onSuccess {
                remoteVersion?.let { version -> saveLocal(version, it) }
                emit(it.successResult(emptyList()))
            }.onFailure { emit(errorResult(emptyList(), null)) }
        }
        
        val localFLow = loadLocalFlow()
        localFLow.combine(remoteFlow) { localData, remoteData ->

        }

        flow<DataResult<T>> {
            val flowErrors = mutableListOf<Throwable>()
            var lastLocalData: AtomicReference<T>? = null
            kotlin.runCatching {
                emit(null.loadingResult(flowErrors))
                loadLocalFlow()
                    .flatMapConcat { localData ->
                        flowErrors.clear()

                        var needCallRemote = true
                        var remoteVersionWithError = false
                        val localVersion =
                            kotlin.runCatching { localVersion() }.onFailure(flowErrors::add)
                                .getOrNull()
                        val remoteVersion = kotlin.runCatching { remoteVersion() }
                            .onSuccess { remoteVersionWithError = false }
                            .onFailure { remoteVersionWithError = true }
                            .onFailure(flowErrors::add)
                            .getOrNull()

                        if (localData is List<*> && localData.isNotEmpty()) {
                            requestApi(remoteVersion, localData, flowErrors)
                            return@flatMapConcat flow<DataResult<T>> {}
                        }

                        if (localVersion != null) {
                            if (localVersion.strategy != remoteVersion?.strategy) {
                                dumpLocal(localVersion)
                            } else {
                                needCallRemote =
                                    isLocalStillValid(localVersion, remoteVersion).not()
                                val isLocalDisplayable = isLocalDisplayable(
                                    localVersion,
                                    remoteVersion
                                )
                                if (remoteVersionWithError || isLocalDisplayable) {
                                    if (needCallRemote) {
                                        Log.wtf("TEST FLOW", "LOCAL DATA, BUT SHOULD REQUEST API")
                                        emit(localData.loadingResult(flowErrors))
                                    } else {
                                        Log.wtf("TEST FLOW", "DATA STILL VALID")
                                        emit(localData.successResult(flowErrors))
                                        return@flatMapConcat flow<DataResult<T>> {}
                                    }
                                } else {
                                    dumpLocal(localVersion)
                                }
                            }
                        }
                        flow<DataResult<T>> {}
                    }
            }.onFailure(flowErrors::add)
                .onFailure { emit(errorResult(flowErrors, lastLocalData?.get())) }
        }
    }
}