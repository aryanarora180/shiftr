package com.example.shiftr.model

import android.content.Context
import android.util.Log
import com.example.shiftr.data.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        OperationResult.Success(result.data)
    } catch (e: HttpException) {
        e.printStackTrace()
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: Exception) {
        e.printStackTrace()
        OperationResult.Error(OperationResult.getErrorMessage(OperationResult.ERROR_CODE_UNDETERMINED))
    }

    override suspend fun addTodo(
        name: String,
        description: String,
        color: String
    ): OperationResult<Todo> = try {
        val result =
            apiClient.addTodo(
                TodoBody(
                    name,
                    if (description.isEmpty()) "-" else description,
                    color
                )
            )
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

    override suspend fun getTodoItems(todoId: Int): OperationResult<List<TodoItem>> = try {
        val result = apiClient.getTodoItems()
        OperationResult.Success(result.data.filter { it.todoId == todoId }
            .sortedWith(compareBy({ it.getEpochTime() }, { it.getPriorityAsInt() })))
    } catch (e: HttpException) {
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: Exception) {
        OperationResult.Error(OperationResult.getErrorMessage(OperationResult.ERROR_CODE_UNDETERMINED))
    }

    override suspend fun addTodoItem(
        itemText: String,
        todoId: Int,
        done: Boolean,
        priority: String,
        deadline: String,
    ): OperationResult<TodoItem> = try {
        val result = apiClient.addTodoItem(TodoItemBody(itemText, todoId, done, priority, deadline))
        OperationResult.Success(result.data)
    } catch (e: HttpException) {
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: Exception) {
        OperationResult.Error(OperationResult.getErrorMessage(OperationResult.ERROR_CODE_UNDETERMINED))
    }

    override suspend fun updateTodoItemDone(
        todoItemId: Int,
        todoListId: Int,
        done: Boolean
    ): OperationResult<Unit> = try {
        apiClient.setTodoItemDone(todoItemId, TodoItemUpdateDoneBody(todoListId, done))
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

    override suspend fun deleteTodoItem(id: Int): OperationResult<Unit> = try {
        apiClient.deleteTodoItem(id)
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

    override suspend fun getInventory(): OperationResult<List<InventoryItem>> = try {
        val result = apiClient.getInventory()
        OperationResult.Success(
            result.data.sortedWith(
                compareBy({ it.category }, { it.name })
            )
        )
    } catch (e: HttpException) {
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: Exception) {
        OperationResult.Error(OperationResult.getErrorMessage(OperationResult.ERROR_CODE_UNDETERMINED))
    }

    override suspend fun addInventoryItem(
        name: String,
        category: String,
        quantity: Float,
        unit: String
    ): OperationResult<Unit> = try {
        val result = apiClient.addInventoryItem(
            InventoryItemBody(
                name,
                category,
                quantity,
                InventoryItem.getUnitFromText(unit)
            )
        )
        OperationResult.Success(result)
    } catch (e: HttpException) {
        e.printStackTrace()
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: Exception) {
        e.printStackTrace()
        OperationResult.Error(OperationResult.getErrorMessage(OperationResult.ERROR_CODE_UNDETERMINED))
    }

    override suspend fun updateInventoryItemQuantity(
        inventoryId: Int,
        newQuantity: Float
    ): OperationResult<Unit> = try {
        val result = apiClient.updateInventoryQuantity(
            inventoryId,
            InventoryItemQuantityUpdateBody(newQuantity)
        )
        OperationResult.Success(result)
    } catch (e: HttpException) {
        e.printStackTrace()
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: Exception) {
        e.printStackTrace()
        OperationResult.Error(OperationResult.getErrorMessage(OperationResult.ERROR_CODE_UNDETERMINED))
    }

    override suspend fun getDashboard(): OperationResult<DashboardResponse> = try {
        val result = apiClient.getDashboard()
        OperationResult.Success(result.data)
    } catch (e: HttpException) {
        e.printStackTrace()
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: Exception) {
        e.printStackTrace()
        OperationResult.Error(OperationResult.getErrorMessage(OperationResult.ERROR_CODE_UNDETERMINED))
    }

    override suspend fun deleteInventoryItem(inventoryId: Int): OperationResult<Unit> = try {
        apiClient.deleteInventoryItem(inventoryId)
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

    override suspend fun uploadDocumentForTodo(todoId: Int, file: MultipartBody.Part) = try {
        apiClient.uploadDocumentForTodo(
            file,
            RequestBody.create(MediaType.parse("text/plain"), todoId.toString())
        )
        OperationResult.Success(Unit)
    } catch (e: HttpException) {
        e.printStackTrace()
        getParsedErrorBody(e.code(), e.response()?.errorBody()?.string())
    } catch (e: KotlinNullPointerException) {
        OperationResult.Success(Unit)
    } catch (e: Exception) {
        OperationResult.Error(OperationResult.getErrorMessage(OperationResult.ERROR_CODE_UNDETERMINED))
    }

    override suspend fun scheduleEmailForTodoItem(todoItemId: Int): OperationResult<Unit> = try {
        val result = apiClient.scheduleEmail(ScheduleEmailTodoItemBody(todoItemId))
        OperationResult.Success(result)
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
                    jsonObject.getString("errors")
                )
            } catch (e: Exception) {
                OperationResult.Error(OperationResult.getErrorMessage(status))
            }
        } else {
            OperationResult.Error(OperationResult.getErrorMessage(status))
        }
    }
}