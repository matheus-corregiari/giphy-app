package com.matheus_corregiari.giphy.feature

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GiphyBrowseViewModel : ViewModel(), LifecycleObserver {

    private val _searchLiveData = MutableLiveData<String>()
    val searchLiveData: LiveData<String>
        get() = _searchLiveData

    val lastTypedTerm: String?
        get() = _searchLiveData.value

    fun searchTerm(term: String) {
        _searchLiveData.value = term
    }
}