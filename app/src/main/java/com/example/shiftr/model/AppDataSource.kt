package com.example.shiftr.model

import com.example.shiftr.data.OperationResult
import com.example.shiftr.data.TodoItem

interface AppDataSource {

    suspend fun getGoodies(): OperationResult<List<TodoItem>>
}