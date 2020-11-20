package com.example.shiftr.model

import com.example.shiftr.data.*
import com.squareup.moshi.Json
import retrofit2.http.Body

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
}