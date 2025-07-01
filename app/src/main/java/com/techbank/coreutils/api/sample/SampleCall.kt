package com.techbank.corelibutils.api.sample

import androidx.lifecycle.LifecycleOwner
import com.techbank.corelibutils.api.ApiCaller
import com.techbank.corelibutils.api.ApiClient
import com.techbank.corelibutils.api.ApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun liveData(owner: LifecycleOwner) {
    val api = ApiClient.create(MyApiService::class.java, "https://mydomain.com/")

    val resultLiveData = ApiCaller.callApiLiveData {
        api.getUserData() // suspend function
    }

    resultLiveData.observe(owner) { response ->
        when (response) {
            is ApiResponse.Loading -> {
                // Loading
            }
            is ApiResponse.Success -> {
                // Success
            }
            is ApiResponse.Error -> {
                // Failure
            }
        }
    }

}

fun flowData() {
    val api = ApiClient.create(MyApiService::class.java, "https://mydomain.com/")
    CoroutineScope(Dispatchers.IO).launch {
        ApiCaller.callApiFlow { api.getUserData() }.collect { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    // Loading
                }
                is ApiResponse.Success -> {
                    // Success
                }
                is ApiResponse.Error -> {
                    // Failure
                }
            }
        }
    }
}