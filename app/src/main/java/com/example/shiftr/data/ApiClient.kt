package com.example.shiftr.data

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

object ApiClient {

    private const val BASE_URL = "https://shiftrio.herokuapp.com/api/v1/"

    private fun okHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .authenticator(AuthInterceptor(context))
            .addInterceptor(AuthInterceptor(context))
            .build()
    }

    private lateinit var apiService: ApiService
    fun build(context: Context): ApiService {
        if (!(ApiClient::apiService.isInitialized)) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(okHttpClient(context))
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

        @POST("auth/token/refresh/")
        suspend fun refreshToken(
            @Body body: RefreshTokenBody,
        ): RefreshTokenResponse

        @GET("todo/")
        suspend fun getTodo(): List<Todo>

        @POST("todo/")
        suspend fun addTodo(
            @Body body: TodoBody
        ): Todo
    }

}