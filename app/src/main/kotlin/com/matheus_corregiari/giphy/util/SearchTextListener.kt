package com.matheus_corregiari.giphy.util

import android.os.Handler
import android.os.Looper
import androidx.appcompat.widget.SearchView

class SearchTextListener(private val delay: Long = 300L, private val onSearch: (String) -> Unit) :
    SearchView.OnQueryTextListener {

    private val handler = Handler(Looper.getMainLooper())
    private var workRunnable: Runnable? = null
    private var lastTypedQuery : String? = null

    override fun onQueryTextSubmit(query: String?): Boolean {
        stopRunning()
        workRunnable = Runnable {
            if(query == lastTypedQuery) return@Runnable
            onSearch.invoke(query ?: "")
            lastTypedQuery = query
        }
        handler.postDelayed(workRunnable!!, delay)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        stopRunning()
        workRunnable = Runnable {
            if(newText == lastTypedQuery) return@Runnable
            onSearch.invoke(newText ?: "")
            lastTypedQuery = newText
        }
        handler.postDelayed(workRunnable!!, delay)
        return true
    }

    private fun stopRunning() {
        if (workRunnable != null) {
            handler.removeCallbacks(workRunnable!!)
            workRunnable = null
        }
    }
}