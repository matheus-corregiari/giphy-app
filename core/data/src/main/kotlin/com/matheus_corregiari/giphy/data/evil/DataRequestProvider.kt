package com.matheus_corregiari.giphy.data.evil

import br.com.arch.toolkit.livedata.response.DataResult
import br.com.arch.toolkit.livedata.response.DataResultStatus
import br.com.arch.toolkit.livedata.response.ResponseLiveData
import com.matheus_corregiari.giphy.data.local.entity.VersionData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal abstract class DataRequestProvider<T> {

    abstract suspend fun remoteLoad(): T
    abstract suspend fun remoteVersion(): VersionData?

    abstract suspend fun localVersion(): VersionData?
    abstract suspend fun localLoad(): T
    abstract suspend fun saveLocal(version: VersionData, data: T)
    abstract suspend fun dump(version: VersionData)

    abstract suspend fun isLocalDataDisplayable(
        localVersion: VersionData,
        remoteVersion: VersionData?
    ): Boolean

    fun asFlow(): Flow<DataResult<T>> {
        return flow {
            withContext(Dispatchers.IO) {
                emit(DataResult<T>(data = null, error = null, status = DataResultStatus.LOADING))
                kotlin.runCatching {
                    executeOperation()
                }.onFailure {
                    emit(
                        DataResult<T>(
                            data = null,
                            error = it,
                            status = DataResultStatus.ERROR
                        )
                    )
                }
            }
        }
    }

    fun asLiveData(): ResponseLiveData<T> {
        val liveData = Bacate<T>()
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                asFlow().collectLatest {
                    liveData.value = it
                }
            }
        }
        return liveData
    }

    private suspend fun FlowCollector<DataResult<T>>.executeOperation() {
        var needCallRemote = true
        var remoteVersionWithError = false
        var localData: T? = null
        val localVersion = localVersion()
        val remoteVersion = kotlin.runCatching { remoteVersion() }
            .onSuccess { remoteVersionWithError = false }
            .onFailure { remoteVersionWithError = true }
            .getOrNull()

        if (localVersion != null) {
            localData = kotlin.runCatching { localLoad() }.onSuccess {
                needCallRemote = localVersion != remoteVersion
                if (remoteVersionWithError || isLocalDataDisplayable(
                        localVersion,
                        remoteVersion
                    )
                ) {
                    if (needCallRemote) {
                        emit(localData.loadingResult())
                    } else {
                        emit(localData.successResult())
                        return
                    }
                } else {
                    dump(localVersion)
                }
            }.getOrNull()
        }
        if (needCallRemote) {
            kotlin.runCatching { remoteLoad() }.onSuccess { newData ->
                emit(newData.successResult())
                if (remoteVersion != null) {
                    kotlin.runCatching { saveLocal(remoteVersion, newData) }
                }
            }.onFailure { error -> emit(error.errorResult(localData)) }
        }
    }

    private fun Throwable.errorResult(data: T?) = DataResult<T>(
        data = data, error = this, status = DataResultStatus.ERROR
    )

    private fun T?.successResult() = DataResult<T>(
        data = this, error = null, status = DataResultStatus.SUCCESS
    )

    private fun T?.loadingResult() = DataResult<T>(
        data = this, error = null, status = DataResultStatus.LOADING
    )
}