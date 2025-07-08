package com.example.securenotes.shared.ui

import androidx.annotation.MainThread
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

@MainThread
class ViewFieldTracked<T>(initialValue: T) {

    val current: MutableLiveData<T> = MutableLiveData(initialValue)
    private var originValue: T = initialValue

    val hasChanged: MediatorLiveData<Boolean> = MediatorLiveData()

    init {
        hasChanged.addSource(current) { checkChange() }
        hasChanged.value = false
    }

    private fun checkChange() {
        hasChanged.value = current.value != originValue
    }

    fun set(newOrigin: T) {
        originValue = newOrigin
        current.value = newOrigin
        hasChanged.value = false
    }

    fun updateOrigin() {
        originValue = current.value!!
        hasChanged.value = false
    }
//    fun getCurrent(): T = current.value ?: originValue
}