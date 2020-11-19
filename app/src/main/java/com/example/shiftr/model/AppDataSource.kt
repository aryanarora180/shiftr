package com.example.shiftr.model

import com.example.shiftr.data.*

interface AppDataSource {

    suspend fun loginUserWithEmail(
        email: String,
        password: String
    ): OperationResult<EmailLoginResponse>

    suspend fun registerUserWithEmail(
        user: RegisterBody
    ): OperationResult<Unit>

    suspend fun loginUserWithGoogle(
        idToken: String,
        phoneNumber: String,
        profession: String,
    ): OperationResult<GoogleLoginResponse>

    suspend fun getTodo(): OperationResult<List<Todo>>
}