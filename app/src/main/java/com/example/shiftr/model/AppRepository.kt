package com.example.shiftr.model

import com.example.shiftr.data.ApiClient
import com.example.shiftr.data.OperationResult
import com.example.shiftr.data.TodoItem
import retrofit2.HttpException

class AppRepository : AppDataSource {

    private val apiClient = ApiClient.build()

    override suspend fun getGoodies(): OperationResult<List<TodoItem>> = try {
        val result = apiClient.getTodo()
        OperationResult.Success(result)
    } catch (e: HttpException) {
        OperationResult.Error(OperationResult.getErrorMessage(e.code()))
    } catch (e: Exception) {
        OperationResult.Error(OperationResult.getErrorMessage(OperationResult.ERROR_CODE_UNDETERMINED))
    }
}