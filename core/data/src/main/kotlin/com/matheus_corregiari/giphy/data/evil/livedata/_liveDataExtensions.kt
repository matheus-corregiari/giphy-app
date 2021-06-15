package com.matheus_corregiari.giphy.data.evil.livedata

import br.com.arch.toolkit.livedata.response.DataResult
import br.com.arch.toolkit.livedata.response.ResponseLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal val <T> Flow<DataResult<T>>.responseLiveData: ResponseLiveData<T>
    get() {
        val liveData = ResultMutableResponseLiveData<T>()
        GlobalScope.launch { this@responseLiveData.collectLatest { liveData.postValue(it) } }
        return liveData
    }
