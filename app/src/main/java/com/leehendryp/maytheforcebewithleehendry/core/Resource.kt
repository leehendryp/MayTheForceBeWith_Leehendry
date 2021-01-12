package com.leehendryp.maytheforcebewithleehendry.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Resource<T> {
    private val _data by lazy { MutableLiveData<T>() }
    val data: LiveData<T> = _data

    private val _error by lazy { MutableLiveData<Throwable>() }
    val error: LiveData<Throwable> = _error

    fun setData(data: T) {
        _data.value = data
    }

    fun setError(error: Throwable) {
        _error.value = error
    }
}