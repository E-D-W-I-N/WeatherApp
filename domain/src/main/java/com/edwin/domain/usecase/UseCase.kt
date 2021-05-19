package com.edwin.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface UseCase<out Type, in Params> {

    suspend fun run(params: Params): Type

    operator fun invoke(params: Params): Flow<Result<Type>> {
        return flow {
            val result = try {
                Result.success(run(params))
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

}