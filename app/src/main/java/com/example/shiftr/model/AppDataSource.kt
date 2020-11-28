package com.example.shiftr.model

import com.example.shiftr.data.*
import okhttp3.MultipartBody

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

    suspend fun addTodo(
        name: String,
        description: String,
        color: String,
    ): OperationResult<Todo>

    suspend fun deleteTodo(
        id: Int,
    ): OperationResult<Unit>

    suspend fun getTodoItems(todoId: Int): OperationResult<List<TodoItem>>

    suspend fun addTodoItem(
        itemText: String,
        todoId: Int,
        done: Boolean,
        priority: String,
        deadline: String,
    ): OperationResult<TodoItem>

    suspend fun updateTodoItemDone(
        todoItemId: Int,
        todoListId: Int,
        done: Boolean,
    ): OperationResult<Unit>

    suspend fun deleteTodoItem(
        id: Int,
    ): OperationResult<Unit>

    suspend fun getInventory(): OperationResult<List<InventoryItem>>

    suspend fun addInventoryItem(
        name: String,
        category: String,
        quantity: Float,
        unit: String,
    ): OperationResult<Unit>

    suspend fun updateInventoryItemQuantity(
        inventoryId: Int,
        newQuantity: Float,
    ): OperationResult<Unit>

    suspend fun deleteInventoryItem(
        inventoryId: Int,
    ): OperationResult<Unit>

    suspend fun getDashboard(): OperationResult<DashboardResponse>

    suspend fun uploadDocumentForTodo(
        todoId: Int,
        file: MultipartBody.Part,
    ): OperationResult<Unit>

    suspend fun scheduleEmailForTodoItem(
        todoItemId: Int,
    ): OperationResult<Unit>
}