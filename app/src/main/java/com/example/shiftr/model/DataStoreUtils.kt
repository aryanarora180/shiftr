package com.example.shiftr.model

import android.content.Context

class DataStoreUtils(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(
        "tokens", Context.MODE_PRIVATE
    )

    private val KEY_ACCESS_TOKEN = "access_token"
    private val KEY_REFRESH_TOKEN = "refresh_token"

    fun getAccessToken(): String? = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    fun storeAccessToken(accessToken: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_ACCESS_TOKEN, "Bearer $accessToken")
            apply()
        }
    }

    fun getRefreshToken(): String? = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    fun storeRefreshToken(refreshToken: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_REFRESH_TOKEN, refreshToken)
            apply()
        }
    }

    fun isSignedIn() = getAccessToken() != null && getRefreshToken() != null

    fun signOut() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}