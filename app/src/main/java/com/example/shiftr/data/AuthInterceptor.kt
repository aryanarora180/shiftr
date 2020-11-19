package com.example.shiftr.data

import android.content.Context
import android.util.Log
import com.example.shiftr.model.DataStoreUtils
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    private val dataStoreUtils = DataStoreUtils(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        // Build the request with the header as the auth token, if it exists
        val mainRequest = chain.request().newBuilder()
        dataStoreUtils.getAccessToken()?.let {
            Log.e(javaClass.simpleName, "Authorization: $it")
            mainRequest.addHeader("Authorization", it)
        }

        // Proceed with the request now
        val mainResponse = chain.proceed(mainRequest.build())

        // Request is unauthorized
//        if (mainResponse.code() == 401) {
//            Log.e(javaClass.simpleName, "401")
//            runBlocking {
//                try {
//                    // Get the new access token and store it
//                    val response = ApiClient.build(context).refreshToken(
//                        RefreshTokenBody(
//                            dataStoreUtils.getRefreshToken() ?: ""
//                        )
//                    )
//                    dataStoreUtils.storeAccessToken(response.newAccessToken)
//
//                    // Switch the authorization header and proceed with the request again
//                    val newMainRequest = mainRequest.header("Authorization", dataStoreUtils.getAccessToken() ?: "")
//                    chain.proceed(newMainRequest.build())
//                } catch (e: Exception) { e.printStackTrace() }
//            }
//        }

        return mainResponse
    }
}