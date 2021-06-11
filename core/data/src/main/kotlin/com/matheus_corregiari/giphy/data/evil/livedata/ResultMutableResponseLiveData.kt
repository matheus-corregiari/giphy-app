package com.matheus_corregiari.giphy.data.evil.livedata

import br.com.arch.toolkit.livedata.response.DataResult
import br.com.arch.toolkit.livedata.response.ResponseLiveData

class ResultMutableResponseLiveData<T> : ResponseLiveData<T>() {
    public override fun setValue(value: DataResult<T>?) {
        super.setValue(value)
    }

    public override fun postValue(value: DataResult<T>?) {
        super.postValue(value)
    }
}