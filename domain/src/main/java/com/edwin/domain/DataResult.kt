package com.edwin.domain

sealed class DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>()
    data class Error(val errorMessage: String) : DataResult<Nothing>()
    object Loading : DataResult<Nothing>()
    object Empty : DataResult<Nothing>()
}