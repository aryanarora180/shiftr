package com.example.shiftr.model

import com.example.shiftr.data.*
import org.json.JSONObject
import retrofit2.HttpException

class AppRepository : AppDataSource {

    private val apiClient = ApiClient.build()

    override suspend fun loginUserWithEmail(
        email: String,
        password: String
    ): OperationResult<GetResponse<LoginResponse>> = try {
        val result = apiClient.loginUserWithEmail(LoginBody(email, password))
        OperationResult.Success(result)
    } catch (e: HttpException) {
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: Exception) {
        e.printStackTrace()
        OperationResult.Error(OperationResult.getErrorMessage(OperationResult.ERROR_CODE_UNDETERMINED))
    }

    private fun getParsedErrorBody(status: Int, errorBody: String?): OperationResult.Error {
        return if (errorBody != null) {
            try {
                val jsonObject = JSONObject(errorBody)
                OperationResult.Error(
                    jsonObject.getJSONObject("errors").getString("detail")
                )
            } catch (e: Exception) {
                OperationResult.Error(OperationResult.getErrorMessage(status))
            }
        } else {
            OperationResult.Error(OperationResult.getErrorMessage(status))
        }
    }
}