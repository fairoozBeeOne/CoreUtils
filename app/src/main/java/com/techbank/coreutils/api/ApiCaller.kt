package com.techbank.corelibutils.api

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import timber.log.Timber

object ApiCaller {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable, "Global Coroutine Exception")
    }

    fun <T : Any> callApiLiveData(
        apiRequest: suspend () -> Response<T>
    ): MutableLiveData<ApiResponse<T>> {
        val liveData = MutableLiveData<ApiResponse<T>>()
        liveData.postValue(ApiResponse.Loading) // ← Emit loading state

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            try {
                val response = apiRequest()
                val result = handleResponse(response)
                liveData.postValue(result)
            } catch (e: Exception) {
                Timber.e(e, "LiveData API error")
                liveData.postValue(ApiResponse.Error(-1, e.localizedMessage ?: "Unknown error"))
            }
        }

        return liveData
    }


    fun <T : Any> callApiFlow(
        apiRequest: suspend () -> Response<T>
    ): Flow<ApiResponse<T>> = flow {
        emit(ApiResponse.Loading) // ← Emit loading state
        try {
            val response = apiRequest()
            emit(handleResponse(response))
        } catch (e: Exception) {
            Timber.e(e, "Flow API error")
            emit(ApiResponse.Error(-1, e.localizedMessage ?: "Unknown error"))
        }
    }

    private fun <T : Any> handleResponse(response: Response<T>): ApiResponse<T> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                ApiResponse.Success(body)
            } else {
                ApiResponse.Error(-1, "Empty response body")
            }
        } else {
            ApiResponse.Error(
                response.code(),
                response.errorBody()?.string() ?: "Unknown API error"
            )
        }
    }
}