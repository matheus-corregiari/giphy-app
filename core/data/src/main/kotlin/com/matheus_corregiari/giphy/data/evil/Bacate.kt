package com.matheus_corregiari.giphy.data.evil

import br.com.arch.toolkit.livedata.response.DataResult
import br.com.arch.toolkit.livedata.response.ResponseLiveData

class Bacate<T> : ResponseLiveData<T>() {
    public override fun setValue(value: DataResult<T>?) {
        super.setValue(value)
    }

    override fun getValue(): DataResult<T>? {
        return super.getValue()
    }
}