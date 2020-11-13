package com.example.shiftr.data

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

object ApiClient {

    private const val BASE_URL = "https://shiftrio.herokuapp.com/api/v1/"

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

        @POST("auth/login/")
        suspend fun loginUserWithEmail(
            @Body bodyEmail: EmailLoginBody,
        ): GetResponse<EmailLoginResponse>

        @POST("auth/register/")
        suspend fun registerUserWithEmail(
            @Body body: RegisterBody,
        ): ResponseBody

        @POST("auth/google-auth/")
        suspend fun loginUserWithGoogle(
            @Body body: GoogleLoginBody,
        ): GetResponse<GoogleLoginResponse>
    }
}