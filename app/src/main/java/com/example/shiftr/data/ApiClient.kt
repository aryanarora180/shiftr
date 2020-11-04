package com.example.shiftr.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

object ApiClient {

    private const val BASE_URL = ""

    private lateinit var apiService: ApiService
    fun build(): ApiService {
        if (!(ApiClient::apiService.isInitialized)) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(OkHttpClient())
                .build()
            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService
    }

    interface ApiService {

        @GET("todo")
        suspend fun getTodo(): List<TodoItem>
    }
}