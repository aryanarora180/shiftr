package com.example.shiftr.data

import android.content.Context
import android.util.Log
import com.example.shiftr.model.DataStoreUtils
import kotlinx.coroutines.runBlocking
import okhttp3.*

class AuthInterceptor(private val context: Context) : Interceptor, Authenticator {

    private val dataStoreUtils = DataStoreUtils(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        // Build the request with the header as the auth token, if it exists
        val mainRequest = chain.request().newBuilder()
        dataStoreUtils.getAccessToken()?.let {
            mainRequest.addHeader("Authorization", it)
        }

        // Proceed with the request now
        return chain.proceed(mainRequest.build())
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        val updatedToken = getUpdatedToken()
        return response.request().newBuilder()
            .header("Authorization", updatedToken)
            .build()
    }

    private fun getUpdatedToken(): String {
        runBlocking {
            try {
                val authTokenResponse = ApiClient.build(context).refreshToken(RefreshTokenBody(dataStoreUtils.getRefreshToken()!!)).newAccessToken
                dataStoreUtils.storeAccessToken(authTokenResponse)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return dataStoreUtils.getAccessToken() ?: ""
    }
}