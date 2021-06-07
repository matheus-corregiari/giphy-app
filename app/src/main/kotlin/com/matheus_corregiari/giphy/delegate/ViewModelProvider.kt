package com.matheus_corregiari.giphy.delegate

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

fun <T : ViewModel> AppCompatActivity.viewModelProvider(kClass: KClass<T>) =
    ViewModelProviderDelegate(kClass, false)

fun <T : ViewModel> Fragment.viewModelProvider(kClass: KClass<T>, fromParent: Boolean = false) =
    ViewModelProviderDelegate(kClass, fromParent)

class ViewModelProviderDelegate<T : ViewModel>(
    private val kClass: KClass<T>,
    private val fromParent: Boolean
) {

    operator fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        return ViewModelProvider(
            thisRef.viewModelStore,
            ViewModelProvider.AndroidViewModelFactory.getInstance(thisRef.application)
        )[kClass.java]
    }

    operator fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return ViewModelProvider(
            (if (fromParent) thisRef.requireActivity() else thisRef).viewModelStore,
            ViewModelProvider.AndroidViewModelFactory.getInstance(thisRef.requireActivity().application)
        )[kClass.java]
    }
}