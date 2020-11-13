package com.example.shiftr.model

import com.example.shiftr.data.GetResponse
import com.example.shiftr.data.LoginResponse
import com.example.shiftr.data.OperationResult

interface AppDataSource {

    suspend fun loginUserWithEmail(
        email: String,
        password: String
    ): OperationResult<GetResponse<LoginResponse>>

    suspend fun registerUserWithEmail(
        email: String,
        username: String,
        password: String,
        phoneNumber: String,
        profession: String,
    ): OperationResult<Unit>
}