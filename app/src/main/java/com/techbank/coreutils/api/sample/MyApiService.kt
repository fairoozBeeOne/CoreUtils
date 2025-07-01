package com.techbank.coreutils.api.sample

import android.service.autofill.UserData
import retrofit2.Response
import retrofit2.http.GET

interface MyApiService {

    @GET("user/profile")
    suspend fun getUserData(): Response<UserData> // ← This is the actual function
}