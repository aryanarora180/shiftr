package com.example.shiftr.model

import com.example.shiftr.data.*
import com.squareup.moshi.Json
import retrofit2.http.Body

interface AppDataSource {

    suspend fun loginUserWithEmail(
        email: String,
        password: String
    ): OperationResult<LoginResponse>

    suspend fun registerUserWithEmail(
        user: RegisterBody
    ): OperationResult<Unit>

    suspend fun loginUserWithGoogle(
        idToken: String,
        phoneNumber: String,
        profession: String,
    ): OperationResult<LoginResponse>
}