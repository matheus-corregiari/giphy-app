package com.matheus_corregiari.giphy.extension

import android.view.View
import android.view.ViewGroup

fun View.enabledRecursively(enable: Boolean) {
    isEnabled = enable
    val count = (this as? ViewGroup)?.childCount ?: 0
    (0..count)
        .mapNotNull { (this as? ViewGroup)?.getChildAt(it) }
        .onEach { it.enabledRecursively(enable) }
}