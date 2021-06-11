package com.matheus_corregiari.giphy.data.evil

import android.util.Log
import br.com.arch.toolkit.livedata.response.DataResult
import br.com.arch.toolkit.livedata.response.DataResultStatus
import br.com.arch.toolkit.livedata.response.ResponseLiveData
import com.matheus_corregiari.giphy.data.evil.livedata.ResultMutableResponseLiveData
import com.matheus_corregiari.giphy.data.exception.MultipleErrorsException
import com.matheus_corregiari.giphy.data.local.storage.entity.VersionData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

internal abstract class DataRequestProvider<T> {

    abstract suspend fun remoteLoad(): T
    abstract suspend fun remoteVersion(): VersionData?

    abstract suspend fun localVersion(): VersionData?
    abstract suspend fun localLoad(): T
    abstract suspend fun saveLocal(version: VersionData, data: T)
    abstract suspend fun dump(version: VersionData)

    abstract suspend fun isVersionsTheSame(
        localVersion: VersionData,
        remoteVersion: VersionData?
    ): Boolean

    abstract suspend fun isLocalDataDisplayable(
        localVersion: VersionData,
        remoteVersion: VersionData?
    ): Boolean

    val flow: Flow<DataResult<T>>
        get() {
            return flow {
                emit(null.loadingResult(null))
                kotlin.runCatching { executeOperation() }
                    .onFailure { emit(errorResult(listOf(it), null)) }
            }.flowOn(Dispatchers.IO)
        }

    val liveData: ResponseLiveData<T>
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
            localData = kotlin.runCatching { localLoad() }.onSuccess { data ->
                needCallRemote = isVersionsTheSame(localVersion, remoteVersion)
                if (remoteVersionWithError || isLocalDataDisplayable(
                        localVersion,
                        remoteVersion
                    )
                ) {
                    if (needCallRemote) {
                        Log.wtf("TEST FLOW", "POSTOU DADOS LOCAIS, MAS ESTÁ CHAMANDO API")
                        emit(data.loadingResult(flowErrors))
                    } else {
                        Log.wtf("TEST FLOW", "DADOS LOCAIS AINDA VÁLIDOS API")
                        emit(data.successResult(flowErrors))
                        return
                    }
                } else {
                    dump(localVersion)
                }
            }.onFailure(flowErrors::add).getOrNull()
        }
        if (needCallRemote) {
            Log.wtf("TEST FLOW", "CHAMOU API")
            kotlin.runCatching { remoteLoad() }.onSuccess { newData ->
                if (remoteVersion != null) {
                    kotlin.runCatching { saveLocal(remoteVersion, newData) }
                        .onFailure(flowErrors::add)
                }
                emit(newData.successResult(flowErrors))
            }.onFailure(flowErrors::add)
                .onFailure { emit(errorResult(flowErrors, localData)) }
        }
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
}