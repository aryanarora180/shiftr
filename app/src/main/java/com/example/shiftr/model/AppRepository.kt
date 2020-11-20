package com.example.shiftr.model

import android.content.Context
import com.example.shiftr.data.*
import org.json.JSONObject
import retrofit2.HttpException

class AppRepository(context: Context) : AppDataSource {

    private val apiClient = ApiClient.build(context)

    override suspend fun loginUserWithEmail(
        email: String,
        password: String
    ): OperationResult<EmailLoginResponse> = try {
        val result = apiClient.loginUserWithEmail(EmailLoginBody(email, password))
        OperationResult.Success(result.data)
    } catch (e: HttpException) {
        e.printStackTrace()
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: Exception) {
        e.printStackTrace()
        OperationResult.Error(OperationResult.getErrorMessage(OperationResult.ERROR_CODE_UNDETERMINED))
    }

    override suspend fun registerUserWithEmail(
        user: RegisterBody
    ): OperationResult<Unit> = try {
        apiClient.registerUserWithEmail(user)
        OperationResult.Success(Unit)
    } catch (e: HttpException) {
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: Exception) {
        OperationResult.Error(OperationResult.getErrorMessage(OperationResult.ERROR_CODE_UNDETERMINED))
    }

    override suspend fun loginUserWithGoogle(
        idToken: String,
        phoneNumber: String,
        profession: String
    ): OperationResult<GoogleLoginResponse> = try {
        val result =
            apiClient.loginUserWithGoogle(GoogleLoginBody(idToken, phoneNumber, profession))
        OperationResult.Success(result.data)
    } catch (e: HttpException) {
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: Exception) {
        OperationResult.Error(OperationResult.getErrorMessage(OperationResult.ERROR_CODE_UNDETERMINED))
    }

    override suspend fun getTodo(): OperationResult<List<Todo>> = try {
        val result = apiClient.getTodo()
        OperationResult.Success(result)
    } catch (e: HttpException) {
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: Exception) {
        OperationResult.Error(OperationResult.getErrorMessage(OperationResult.ERROR_CODE_UNDETERMINED))
    }

    override suspend fun addTodo(
        name: String,
        description: String,
        color: String
    ): OperationResult<Todo> = try {
        val result =
            apiClient.addTodo(TodoBody(name, description, color))
        OperationResult.Success(result)
    } catch (e: HttpException) {
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: Exception) {
        OperationResult.Error(OperationResult.getErrorMessage(OperationResult.ERROR_CODE_UNDETERMINED))
    }

    override suspend fun deleteTodo(id: Int): OperationResult<Unit> = try {
        apiClient.deleteTodo(id)
        OperationResult.Success(Unit)
    } catch (e: HttpException) {
        e.printStackTrace()
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: KotlinNullPointerException) {
        OperationResult.Success(Unit)
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