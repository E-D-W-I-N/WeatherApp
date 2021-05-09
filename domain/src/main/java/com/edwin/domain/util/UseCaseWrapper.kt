package com.edwin.domain.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

inline fun <T> flowWithResult(crossinline function: () -> T): Flow<Result<T>> {
    return flow {
        val fusedLocation = try {
            Result.success(function())
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(fusedLocation)
    }.flowOn(Dispatchers.IO)
}