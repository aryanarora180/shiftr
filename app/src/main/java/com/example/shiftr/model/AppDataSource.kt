package com.example.shiftr.model

import com.example.shiftr.data.GetResponse
import com.example.shiftr.data.LoginResponse
import com.example.shiftr.data.OperationResult
import com.example.shiftr.data.RegisterBody

interface AppDataSource {

    suspend fun loginUserWithEmail(
        email: String,
        password: String
    ): OperationResult<GetResponse<LoginResponse>>

    suspend fun registerUserWithEmail(
        user: RegisterBody
    ): OperationResult<Unit>
}